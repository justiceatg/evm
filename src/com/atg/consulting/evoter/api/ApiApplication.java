/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.api;

import com.atg.consulting.evoter.CommonUtil;
import com.atg.consulting.evoter.RestletUtil;
import com.atg.consulting.evoter.business.services.Cache;
import com.atg.consulting.evoter.domain.LoginSession;
import com.atg.consulting.evoter.domain.User;
import com.atg.consulting.evoter.domain.UserRole;
import com.atg.consulting.evoter.entitydata.LoginSessionData;
import com.atg.consulting.evoter.entitydata.UserData;
import com.atg.consulting.evoter.jpa.JPAFactory;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.routing.Router;

/**
 *
 * @author Brian
 */
public class ApiApplication extends Application {

    protected DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    protected DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");

    @Override
    public Restlet createInboundRoot() {
        Router router = new Router(getContext());

        router.attach("/login/{username}/{password}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String username = RestletUtil.getParameter(request, "username").toLowerCase().trim();
                    String password = RestletUtil.getParameter(request, "password");

                    User user = JPAFactory.getInstance().getUserJpaController().findUser(username.toLowerCase());
                    try {
                        String digest = CommonUtil.getSHADigest(password);
                        if (user != null && user.getPassword().equals(digest)) {
                            LoginSession ls = new LoginSession();
                            UUID uuid = UUID.randomUUID();
                            boolean uniqueSessionId = true;
                            while (uniqueSessionId) {
                                uniqueSessionId = false;
                                String id = uuid.toString() + (new Date()).getTime();
                                ls.setId(id.replace("-", ""));
                                ls.setUser(user);
                                try {
                                    JPAFactory.getInstance().getLoginSessionJpaController().create(ls);
                                } catch (Exception ex) {
                                    uniqueSessionId = true;
                                }
                            }
                            Cache.getInstance().addSession(ls);
                            response.setEntity(new JacksonRepresentation(new LoginSessionData(ls)));
                            response.setStatus(Status.SUCCESS_OK);
                        } else {
                            Logger.getLogger(ApiApplication.class.getName()).log(Level.INFO, "Failed to login");
                        }
                    } catch (Exception ex) {
                        response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                    }
                } catch (UnsupportedEncodingException ex) {
                    response.setStatus(Status.SERVER_ERROR_INTERNAL);
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/logout/{session}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");

                    LoginSession loginSession = Cache.getInstance().getSession(session);

                    if (loginSession != null) {
                        Cache.getInstance().remove(session);
                        response.setStatus(Status.SUCCESS_OK);
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/getLoggedOnUser/{session}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null) {
                        try {
                            response.setEntity(new JacksonRepresentation(new UserData(loginSession.getUser())));
                            response.setStatus(Status.SUCCESS_OK);
                        } catch (Exception e) {
                            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        }
                    } else {
                        response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/registerUser/{session}/{fullNames}/{username}/{password}/{userRole}/{email}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    String fullNames = RestletUtil.getParameter(request, "fullNames");
                    String username = RestletUtil.getParameter(request, "username").toLowerCase().trim();
                    String password = RestletUtil.getParameter(request, "password");
                    String userRole = RestletUtil.getParameter(request, "userRole");
                    String email = RestletUtil.getParameter(request, "email");
                    UserRole ur = Enum.valueOf(UserRole.class, userRole);

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            User user = new User();
                            user.setUsername(username);
                            user.setEmailAddress(email);
                            user.setPassword(CommonUtil.getSHADigest(password));
                            user.setFullnames(fullNames);
                            user.setUserRole(ur);
                            JPAFactory.getInstance().getUserJpaController().create(user);
                            UserData ud = new UserData(user);
                            response.setEntity(new JacksonRepresentation(ud));
                            response.setStatus(Status.SUCCESS_OK);
                        } catch (Exception e) {
                            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        }
                    }
                } catch (UnsupportedEncodingException ex) {
                    response.setStatus(Status.SERVER_ERROR_INTERNAL);
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/changePassword/{session}/{username}/{password}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    String username = RestletUtil.getParameter(request, "username");
                    String password = RestletUtil.getParameter(request, "password");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            User user = JPAFactory.getInstance().getUserJpaController().findUser(username);
                            user.setPassword(CommonUtil.getSHADigest(password));
                            JPAFactory.getInstance().getUserJpaController().edit(user);
                            response.setEntity(new JacksonRepresentation(new UserData(user)));
                            response.setStatus(Status.SUCCESS_OK);
                        } catch (Exception e) {
                            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        }
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/updateUser/{session}/{username}/{fullNames}/{address}/{cellphone}/{emailAddress}/{nationalId}/{postalAddress}/{userRole}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    String username = RestletUtil.getParameter(request, "username");
                    String fullNames = RestletUtil.getParameter(request, "fullNames");
                    String address = RestletUtil.getParameter(request, "address");
                    String cellphone = RestletUtil.getParameter(request, "cellphone");
                    String emailAddress = RestletUtil.getParameter(request, "emailAddress");
                    String nationalId = RestletUtil.getParameter(request, "nationalId");
                    String postalAddress = RestletUtil.getParameter(request, "postalAddress");
                    String userRole = RestletUtil.getParameter(request, "userRole");
                    UserRole ur = Enum.valueOf(UserRole.class, userRole);

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            User user = JPAFactory.getInstance().getUserJpaController().findUser(username);
                            user.setFullnames(fullNames);
                            user.setAddress(address);
                            user.setCellphone(cellphone);
                            user.setEmailAddress(emailAddress);
                            user.setNationalId(nationalId);
                            user.setPostalAddress(postalAddress);
                            user.setUserRole(ur);
                            JPAFactory.getInstance().getUserJpaController().edit(user);
                            response.setEntity(new JacksonRepresentation(new UserData(user)));
                            response.setStatus(Status.SUCCESS_OK);
                        } catch (Exception e) {
                            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        }
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/deleteUser/{session}/{username}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    String username = RestletUtil.getParameter(request, "username");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            JPAFactory.getInstance().getUserJpaController().destroy(username);
                            response.setStatus(Status.SUCCESS_OK);
                        } catch (Exception e) {
                            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        }
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/getAllUsers/{session}/{max}/{index}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            int index = Integer.parseInt(RestletUtil.getParameter(request, "index"));
                            int max = Integer.parseInt(RestletUtil.getParameter(request, "max"));

                            List<User> users = JPAFactory.getInstance().getUserJpaController().findUserEntities(max, index);

                            UserData[] data = new UserData[users.size()];
                            int k = 0;
                            for (User u : users) {
                                data[k] = new UserData(u);
                                k++;
                            }
                            response.setEntity(new JacksonRepresentation(data));
                            response.setStatus(Status.SUCCESS_OK);
                        } catch (UnsupportedEncodingException | NumberFormatException e) {
                            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        }
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/getUser/{session}/{username}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    String username = RestletUtil.getParameter(request, "username");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            User user = JPAFactory.getInstance().getUserJpaController().findUser(username);
                            response.setEntity(new JacksonRepresentation(new UserData(user)));
                            response.setStatus(Status.SUCCESS_OK);
                        } catch (Exception e) {
                            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        }
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        return router;
    }
}
