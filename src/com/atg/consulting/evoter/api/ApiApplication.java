/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.api;

import com.atg.consulting.evoter.CommonUtil;
import com.atg.consulting.evoter.RestletUtil;
import com.atg.consulting.evoter.business.services.Cache;
import com.atg.consulting.evoter.domain.Candidate;
import com.atg.consulting.evoter.domain.Constituency;
import com.atg.consulting.evoter.domain.Constituency.ConstituencyType;
import com.atg.consulting.evoter.domain.Election;
import com.atg.consulting.evoter.domain.Election.ElectionType;
import com.atg.consulting.evoter.domain.ElectionOfficer;
import com.atg.consulting.evoter.domain.GeneralElection;
import com.atg.consulting.evoter.domain.LoginSession;
import com.atg.consulting.evoter.domain.User;
import com.atg.consulting.evoter.domain.UserRole;
import com.atg.consulting.evoter.domain.Vote;
import com.atg.consulting.evoter.domain.Voter;
import com.atg.consulting.evoter.domain.VoterRegistration;
import com.atg.consulting.evoter.entitydata.CandidateData;
import com.atg.consulting.evoter.entitydata.ConstituencyData;
import com.atg.consulting.evoter.entitydata.ElectionData;
import com.atg.consulting.evoter.entitydata.ElectionOfficerData;
import com.atg.consulting.evoter.entitydata.LoginSessionData;
import com.atg.consulting.evoter.entitydata.UserData;
import com.atg.consulting.evoter.entitydata.VoteData;
import com.atg.consulting.evoter.entitydata.VoterData;
import com.atg.consulting.evoter.entitydata.VoterRegistrationData;
import com.atg.consulting.evoter.jpa.JPAFactory;
import com.atg.consulting.evoter.jpa.exceptions.NonexistentEntityException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
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

                    User user = JPAFactory.getInstance().getUserJpaController().findUserByName(username.toLowerCase());
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
                            User user = JPAFactory.getInstance().getUserJpaController().findUserByName(username);
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

        router.attach("/updateUser/{session}/{userId}/{fullNames}/{address}/{cellphone}/{emailAddress}/{nationalId}/{postalAddress}/{userRole}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long userId = Long.valueOf(RestletUtil.getParameter(request, "userId"));
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
                            User user = JPAFactory.getInstance().getUserJpaController().findUser(userId);
                            user.setFullnames(fullNames);
                            user.setUserRole(ur);
                            if (!nationalId.equals("null")) {
                                user.setNationalId(nationalId);
                            }
                            if (!address.equals("null")) {
                                user.setAddress(address);
                            }
                            if (!cellphone.equals("null")) {
                                user.setCellphone(cellphone);
                            }
                            if (!emailAddress.equals("null")) {
                                user.setEmailAddress(emailAddress);
                            }
                            if (!postalAddress.equals("null")) {
                                user.setPostalAddress(postalAddress);
                            }
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

        router.attach("/deleteUser/{session}/{userId}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long userId = Long.valueOf(RestletUtil.getParameter(request, "userId"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            JPAFactory.getInstance().getUserJpaController().destroy(userId);
                            response.setStatus(Status.SUCCESS_OK);
                        } catch (NonexistentEntityException ex) {
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

        router.attach("/getUser/{session}/{userId}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long userId = Long.valueOf(RestletUtil.getParameter(request, "userId"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            User user = JPAFactory.getInstance().getUserJpaController().findUser(userId);
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

        router.attach("/searchUsers/{searchText}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String searchText = RestletUtil.getParameter(request, "searchText");

                    List<User> users = JPAFactory.getInstance().getUserJpaController().findUserByUsername(searchText);

                    UserData[] data = new UserData[users.size()];
                    int k = 0;
                    for (User u : users) {
                        data[k] = new UserData(u);
                        k++;
                    }

                    response.setEntity(new JacksonRepresentation(data));
                    response.setStatus(Status.SUCCESS_OK);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/addCandidate/{session}/{candidateNumber}/{fullnames}/{nationalId}/{election}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    int candidateNumber = Integer.parseInt(RestletUtil.getParameter(request, "candidateNumber"));
                    String fullnames = RestletUtil.getParameter(request, "fullnames");
                    String nationalId = RestletUtil.getParameter(request, "nationalId");
                    Long electionId = Long.parseLong(RestletUtil.getParameter(request, "election"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Election election = JPAFactory.getInstance().getElectionJpaController().findElection(electionId);

                            Candidate candidate = new Candidate();
                            candidate.setCandidateNumber(candidateNumber);
                            candidate.setFullnames(fullnames);
                            candidate.setNationalId(nationalId);
                            candidate.setElection(election);
                            JPAFactory.getInstance().getCandidateJpaController().create(candidate);
                            response.setEntity(new JacksonRepresentation(new CandidateData(candidate)));
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

        router.attach("/updateCandidate/{session}/{candidate}/{candidateNumber}/{fullnames}/{nationalId}/{election}/{address}/{cellphone}/{postalAddress}/{emailAddress}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long candidateId = Long.parseLong(RestletUtil.getParameter(request, "candidate"));
                    int candidateNumber = Integer.parseInt(RestletUtil.getParameter(request, "candidateNumber"));
                    String fullnames = RestletUtil.getParameter(request, "fullnames");
                    String nationalId = RestletUtil.getParameter(request, "nationalId");
                    Long electionId = Long.parseLong(RestletUtil.getParameter(request, "election"));
                    String address = RestletUtil.getParameter(request, "address");
                    String cellphone = RestletUtil.getParameter(request, "cellphone");
                    String postalAddress = RestletUtil.getParameter(request, "postalAddress");
                    String emailAddress = RestletUtil.getParameter(request, "emailAddress");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Election election = JPAFactory.getInstance().getElectionJpaController().findElection(electionId);

                            Candidate candidate = JPAFactory.getInstance().getCandidateJpaController().findCandidate(candidateId);
                            candidate.setCandidateNumber(candidateNumber);
                            candidate.setFullnames(fullnames);
                            candidate.setNationalId(nationalId);
                            candidate.setElection(election);
                            candidate.setAddress(address);
                            candidate.setCellphone(cellphone);
                            candidate.setPostalAddress(postalAddress);
                            candidate.setEmailAddress(emailAddress);
                            JPAFactory.getInstance().getCandidateJpaController().edit(candidate);
                            response.setEntity(new JacksonRepresentation(new CandidateData(candidate)));
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

        router.attach("/deleteCandidate/{session}/{candidate}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long candidateId = Long.parseLong(RestletUtil.getParameter(request, "candidate"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            JPAFactory.getInstance().getCandidateJpaController().destroy(candidateId);
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

        router.attach("/getAllCandidates/{session}/{max}/{index}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            int index = Integer.parseInt(RestletUtil.getParameter(request, "index"));
                            int max = Integer.parseInt(RestletUtil.getParameter(request, "max"));

                            List<Candidate> candidates = JPAFactory.getInstance().getCandidateJpaController().findCandidateEntities(max, index);

                            CandidateData[] data = new CandidateData[candidates.size()];
                            int k = 0;
                            for (Candidate c : candidates) {
                                data[k] = new CandidateData(c);
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

        router.attach("/getCandidate/{session}/{candidate}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long candidateId = Long.parseLong(RestletUtil.getParameter(request, "candidate"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Candidate candidate = JPAFactory.getInstance().getCandidateJpaController().findCandidate(candidateId);
                            response.setEntity(new JacksonRepresentation(new CandidateData(candidate)));
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

        router.attach("/searchCandidates/{searchText}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String searchText = RestletUtil.getParameter(request, "searchText");

                    List<Candidate> candidates = JPAFactory.getInstance().getCandidateJpaController().findCandidateByNationalId(searchText);

                    CandidateData[] data = new CandidateData[candidates.size()];
                    int k = 0;
                    for (Candidate c : candidates) {
                        data[k] = new CandidateData(c);
                        k++;
                    }

                    response.setEntity(new JacksonRepresentation(data));
                    response.setStatus(Status.SUCCESS_OK);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/addConstituency/{session}/{name}/{constituencyType}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {

                    String session = RestletUtil.getParameter(request, "session");
                    String name = RestletUtil.getParameter(request, "name");
                    String cT = RestletUtil.getParameter(request, "constituencyType");
                    ConstituencyType constituencyType = Enum.valueOf(ConstituencyType.class, cT);

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {

                            Constituency constituency = new Constituency();
                            constituency.setName(name);
                            constituency.setConstituencyType(constituencyType);
                            JPAFactory.getInstance().getConstituencyJpaController().create(constituency);
                            response.setEntity(new JacksonRepresentation(new ConstituencyData(constituency)));
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

        router.attach("/updateConstituency/{session}/{constituency}/{name}/{constituencyType}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long constituencyId = Long.parseLong(RestletUtil.getParameter(request, "constituency"));
                    String name = RestletUtil.getParameter(request, "name");
                    String cT = RestletUtil.getParameter(request, "constituencyType");
                    ConstituencyType constituencyType = Enum.valueOf(ConstituencyType.class, cT);

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Constituency constituency = JPAFactory.getInstance().getConstituencyJpaController().findConstituency(constituencyId);
                            constituency.setName(name);
                            constituency.setConstituencyType(constituencyType);
                            JPAFactory.getInstance().getConstituencyJpaController().edit(constituency);
                            response.setEntity(new JacksonRepresentation(new ConstituencyData(constituency)));
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

        router.attach("/deleteConstituency/{session}/{constituency}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long constituencyId = Long.parseLong(RestletUtil.getParameter(request, "constituency"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            JPAFactory.getInstance().getConstituencyJpaController().destroy(constituencyId);
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

        router.attach("/getAllConstituencies/{session}/{max}/{index}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            int index = Integer.parseInt(RestletUtil.getParameter(request, "index"));
                            int max = Integer.parseInt(RestletUtil.getParameter(request, "max"));

                            List<Constituency> constituencies = JPAFactory.getInstance().getConstituencyJpaController().findConstituencyEntities(max, index);

                            ConstituencyData[] data = new ConstituencyData[constituencies.size()];
                            int k = 0;
                            for (Constituency c : constituencies) {
                                data[k] = new ConstituencyData(c);
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

        router.attach("/getConstituency/{session}/{constituency}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long constituencyId = Long.parseLong(RestletUtil.getParameter(request, "constituency"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Constituency constituency = JPAFactory.getInstance().getConstituencyJpaController().findConstituency(constituencyId);
                            response.setEntity(new JacksonRepresentation(new ConstituencyData(constituency)));
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

        router.attach("/searchConstituencies/{searchText}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String searchText = RestletUtil.getParameter(request, "searchText");

                    List<Constituency> constituencies = JPAFactory.getInstance().getConstituencyJpaController().findConstituenciesByName(searchText);

                    ConstituencyData[] data = new ConstituencyData[constituencies.size()];
                    int k = 0;
                    for (Constituency c : constituencies) {
                        data[k] = new ConstituencyData(c);
                        k++;
                    }

                    response.setEntity(new JacksonRepresentation(data));
                    response.setStatus(Status.SUCCESS_OK);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/addElection/{session}/{constituency}/{startDate}/{endDate}/{electionType}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long constituencyId = Long.parseLong(RestletUtil.getParameter(request, "constituency"));
                    Date startDate = dateFormat.parse(RestletUtil.getParameter(request, "startDate"));
                    Date endDate = dateFormat.parse(RestletUtil.getParameter(request, "endDate"));
                    String cT = RestletUtil.getParameter(request, "electionType");
                    ElectionType electionType = Enum.valueOf(ElectionType.class, cT);

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Constituency constituency = JPAFactory.getInstance().getConstituencyJpaController().findConstituency(constituencyId);

                            Election election = new Election();
                            election.setConstituency(constituency);
                            election.setStartDate(startDate);
                            election.setEndDate(endDate);
                            election.setElectionType(electionType);
                            JPAFactory.getInstance().getElectionJpaController().create(election);
                            response.setEntity(new JacksonRepresentation(new ElectionData(election)));
                            response.setStatus(Status.SUCCESS_OK);
                        } catch (Exception e) {
                            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        }
                    }
                } catch (UnsupportedEncodingException ex) {
                    response.setStatus(Status.SERVER_ERROR_INTERNAL);
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/updateElection/{session}/{election}/{constituency}/{startDate}/{endDate}/{electionType}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long electionId = Long.parseLong(RestletUtil.getParameter(request, "election"));
                    Long constituencyId = Long.parseLong(RestletUtil.getParameter(request, "constituency"));
                    Date startDate = dateFormat.parse(RestletUtil.getParameter(request, "startDate"));
                    Date endDate = dateFormat.parse(RestletUtil.getParameter(request, "endDate"));
                    String cT = RestletUtil.getParameter(request, "electionType");
                    ElectionType electionType = Enum.valueOf(ElectionType.class, cT);

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Constituency constituency = JPAFactory.getInstance().getConstituencyJpaController().findConstituency(constituencyId);

                            Election election = JPAFactory.getInstance().getElectionJpaController().findElection(electionId);
                            election.setConstituency(constituency);
                            election.setStartDate(startDate);
                            election.setEndDate(endDate);
                            election.setElectionType(electionType);
                            JPAFactory.getInstance().getElectionJpaController().edit(election);
                            response.setEntity(new JacksonRepresentation(new ElectionData(election)));
                            response.setStatus(Status.SUCCESS_OK);
                        } catch (Exception e) {
                            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        }
                    }
                } catch (UnsupportedEncodingException | ParseException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/deleteElection/{session}/{election}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long electionId = Long.parseLong(RestletUtil.getParameter(request, "election"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            JPAFactory.getInstance().getElectionJpaController().destroy(electionId);
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

        router.attach("/getAllElections/{session}/{max}/{index}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            int index = Integer.parseInt(RestletUtil.getParameter(request, "index"));
                            int max = Integer.parseInt(RestletUtil.getParameter(request, "max"));

                            List<Election> elections = JPAFactory.getInstance().getElectionJpaController().findElectionEntities(max, index);

                            ElectionData[] data = new ElectionData[elections.size()];
                            int k = 0;
                            for (Election e : elections) {
                                data[k] = new ElectionData(e);
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

        router.attach("/getElection/{session}/{election}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long electionId = Long.parseLong(RestletUtil.getParameter(request, "election"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Election election = JPAFactory.getInstance().getElectionJpaController().findElection(electionId);
                            response.setEntity(new JacksonRepresentation(new ElectionData(election)));
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

        router.attach("/searchElections/{searchText}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String searchText = RestletUtil.getParameter(request, "searchText");

                    List<Election> elections = JPAFactory.getInstance().getElectionJpaController().findElectionsByConstituencyName(searchText);

                    ElectionData[] data = new ElectionData[elections.size()];
                    int k = 0;
                    for (Election e : elections) {
                        data[k] = new ElectionData(e);
                        k++;
                    }

                    response.setEntity(new JacksonRepresentation(data));
                    response.setStatus(Status.SUCCESS_OK);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/addElectionOfficer/{session}/{fullnames}/{nationalId}/{election}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    String fullnames = RestletUtil.getParameter(request, "fullnames");
                    String nationalId = RestletUtil.getParameter(request, "nationalId");
                    Long electionId = Long.parseLong(RestletUtil.getParameter(request, "election"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Election election = JPAFactory.getInstance().getElectionJpaController().findElection(electionId);

                            ElectionOfficer electionOfficer = new ElectionOfficer();
                            electionOfficer.setFullnames(fullnames);
                            electionOfficer.setNationalId(nationalId);
                            electionOfficer.setElection(election);
                            JPAFactory.getInstance().getElectionOfficerJpaController().create(electionOfficer);
                            response.setEntity(new JacksonRepresentation(new ElectionOfficerData(electionOfficer)));
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

        router.attach("/updateElectionOfficer/{session}/{electionOfficer}/{fullnames}/{nationalId}/{election}/{address}/{cellphone}/{postalAddress}/{emailAddress}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {

                    String session = RestletUtil.getParameter(request, "session");
                    Long electionOfficerId = Long.parseLong(RestletUtil.getParameter(request, "electionOfficer"));
                    String fullnames = RestletUtil.getParameter(request, "fullnames");
                    String nationalId = RestletUtil.getParameter(request, "nationalId");
                    Long electionId = Long.parseLong(RestletUtil.getParameter(request, "election"));
                    String address = RestletUtil.getParameter(request, "address");
                    String cellphone = RestletUtil.getParameter(request, "cellphone");
                    String postalAddress = RestletUtil.getParameter(request, "postalAddress");
                    String emailAddress = RestletUtil.getParameter(request, "emailAddress");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            ElectionOfficer electionOfficer = JPAFactory.getInstance().getElectionOfficerJpaController().findElectionOfficer(electionOfficerId);
                            Election election = JPAFactory.getInstance().getElectionJpaController().findElection(electionId);

                            ElectionOfficer electionOfficer1 = JPAFactory.getInstance().getElectionOfficerJpaController().findElectionOfficer(electionOfficerId);
                            electionOfficer.setFullnames(fullnames);
                            electionOfficer.setNationalId(nationalId);
                            electionOfficer.setElection(election);
                            electionOfficer.setAddress(address);
                            electionOfficer.setCellphone(cellphone);
                            electionOfficer.setPostalAddress(postalAddress);
                            electionOfficer.setEmailAddress(emailAddress);
                            JPAFactory.getInstance().getElectionOfficerJpaController().edit(electionOfficer);
                            response.setEntity(new JacksonRepresentation(new ElectionOfficerData(electionOfficer)));
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

        router.attach("/deleteElectionOfficer/{session}/{electionOfficer}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long electionOfficerId = Long.parseLong(RestletUtil.getParameter(request, "electionOfficer"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            JPAFactory.getInstance().getElectionOfficerJpaController().destroy(electionOfficerId);
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

        router.attach("/getAllElectionOfficers/{session}/{max}/{index}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            int index = Integer.parseInt(RestletUtil.getParameter(request, "index"));
                            int max = Integer.parseInt(RestletUtil.getParameter(request, "max"));

                            List<ElectionOfficer> electionOfficers = JPAFactory.getInstance().getElectionOfficerJpaController().findElectionOfficerEntities(max, index);

                            ElectionOfficerData[] data = new ElectionOfficerData[electionOfficers.size()];
                            int k = 0;
                            for (ElectionOfficer eo : electionOfficers) {
                                data[k] = new ElectionOfficerData(eo);
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

        router.attach("/getElectionOfficer/{session}/{electionOfficer}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long electionOfficerId = Long.parseLong(RestletUtil.getParameter(request, "electionOfficer"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            ElectionOfficer electionOfficer = JPAFactory.getInstance().getElectionOfficerJpaController().findElectionOfficer(electionOfficerId);
                            response.setEntity(new JacksonRepresentation(new ElectionOfficerData(electionOfficer)));
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

        router.attach("/searchElectionOfficers/{searchText}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String searchText = RestletUtil.getParameter(request, "searchText");

                    List<ElectionOfficer> electionOfficers = JPAFactory.getInstance().getElectionOfficerJpaController().findElectionOfficersByNationalId(searchText);

                    ElectionOfficerData[] data = new ElectionOfficerData[electionOfficers.size()];
                    int k = 0;
                    for (ElectionOfficer eo : electionOfficers) {
                        data[k] = new ElectionOfficerData(eo);
                        k++;
                    }

                    response.setEntity(new JacksonRepresentation(data));
                    response.setStatus(Status.SUCCESS_OK);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/addVote/{session}/{election}/{voterRegistration}/{voted}/{voteNumber}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long electionId = Long.parseLong(RestletUtil.getParameter(request, "election"));
                    Long voterRegistrationId = Long.parseLong(RestletUtil.getParameter(request, "voterRegistration"));
                    Long votedId = Long.parseLong(RestletUtil.getParameter(request, "voted"));
                    int voteNumber = Integer.parseInt(RestletUtil.getParameter(request, "voteNumber"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Election election = JPAFactory.getInstance().getElectionJpaController().findElection(electionId);
                            VoterRegistration voterRegistration = JPAFactory.getInstance().getVoterRegistrationJpaController().findVoterRegistration(voterRegistrationId);
                            Candidate candidate = JPAFactory.getInstance().getCandidateJpaController().findCandidate(votedId);

                            //TODO: set values for signature and randomVerifier
                            Vote vote = new Vote();
                            vote.setElection(election);
                            vote.setVoterRegistration(voterRegistration);
                            vote.setVoted(candidate);
                            vote.setVoteNumber(voteNumber);
                            JPAFactory.getInstance().getVoteJpaController().create(vote);
                            response.setEntity(new JacksonRepresentation(new VoteData(vote)));
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

//        router.attach("/updateVote/{session}/{vote}/{election}/{voterRegistration}/{voted}/{voteNumber}", new Restlet() {
//
//            @Override
//            public void handle(Request request, Response response) {
//                try {
//
//                    String session = RestletUtil.getParameter(request, "session");
//                    Long voteId = Long.parseLong(RestletUtil.getParameter(request, "vote"));
//                    Long electionId = Long.parseLong(RestletUtil.getParameter(request, "election"));
//                    Long voterRegistrationId = Long.parseLong(RestletUtil.getParameter(request, "voterRegistration"));
//                    Long votedId = Long.parseLong(RestletUtil.getParameter(request, "voted"));
//                    int voteNumber = Integer.parseInt(RestletUtil.getParameter(request, "voteNumber"));
//
//                    LoginSession loginSession = Cache.getInstance().getSession(session);
//                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
//                        try {
//                            Election election = JPAFactory.getInstance().getElectionJpaController().findElection(electionId);
//                            VoterRegistration voterRegistration = JPAFactory.getInstance().getVoterRegistrationJpaController().findVoterRegistration(voterRegistrationId);
//                            Candidate candidate = JPAFactory.getInstance().getCandidateJpaController().findCandidate(votedId);
//
//                            Vote vote = JPAFactory.getInstance().getVoteJpaController().findVote(voteId);
//                            vote.setElection(election);
//                            vote.setVoterRegistration(voterRegistration);
//                            vote.setVoted(candidate);
//                            vote.setVoteNumber(voteNumber);
//                            JPAFactory.getInstance().getVoteJpaController().edit(vote);
//                            response.setEntity(new JacksonRepresentation(new VoteData(vote)));
//                            response.setStatus(Status.SUCCESS_OK);
//                        } catch (Exception e) {
//                            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//                        }
//                    }
//                } catch (UnsupportedEncodingException ex) {
//                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });
//
//        router.attach("/deleteVote/{session}/{vote}", new Restlet() {
//
//            @Override
//            public void handle(Request request, Response response) {
//                try {
//                    String session = RestletUtil.getParameter(request, "session");
//                    Long voteId = Long.parseLong(RestletUtil.getParameter(request, "vote"));
//
//                    LoginSession loginSession = Cache.getInstance().getSession(session);
//                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
//                        try {
//                            JPAFactory.getInstance().getVoteJpaController().destroy(voteId);
//                            response.setStatus(Status.SUCCESS_OK);
//                        } catch (Exception e) {
//                            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//                        }
//                    }
//                } catch (UnsupportedEncodingException ex) {
//                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });
        router.attach("/getAllVotes/{session}/{max}/{index}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            int index = Integer.parseInt(RestletUtil.getParameter(request, "index"));
                            int max = Integer.parseInt(RestletUtil.getParameter(request, "max"));

                            List<Vote> votes = JPAFactory.getInstance().getVoteJpaController().findVoteEntities(max, index);

                            VoteData[] data = new VoteData[votes.size()];
                            int k = 0;
                            for (Vote v : votes) {
                                data[k] = new VoteData(v);
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

        router.attach("/getVote/{session}/{vote}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long voteId = Long.parseLong(RestletUtil.getParameter(request, "vote"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Vote vote = JPAFactory.getInstance().getVoteJpaController().findVote(voteId);
                            response.setEntity(new JacksonRepresentation(new VoteData(vote)));
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

        router.attach("/addVoter/{session}/{fullnames}/{nationalId}/{election}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {

                    String session = RestletUtil.getParameter(request, "session");
                    String fullnames = RestletUtil.getParameter(request, "fullnames");
                    String nationalId = RestletUtil.getParameter(request, "nationalId");
                    Long electionId = Long.parseLong(RestletUtil.getParameter(request, "election"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Election election = JPAFactory.getInstance().getElectionJpaController().findElection(electionId);

                            Voter voter = new Voter();
                            voter.setFullnames(fullnames);
                            voter.setNationalId(nationalId);
                            Long addedVoterId = JPAFactory.getInstance().getVoterJpaController().createAndReturnId(voter);

                            Voter addedVoter = JPAFactory.getInstance().getVoterJpaController().findVoter(addedVoterId);

                            VoterRegistration registration = new VoterRegistration();
                            registration.setVoter(addedVoter);
                            registration.setElection(election);
                            JPAFactory.getInstance().getVoterRegistrationJpaController().create(registration);

                            response.setEntity(new JacksonRepresentation(new VoterData(voter)));
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

        router.attach("/updateVoter/{session}/{voter}/{fullnames}/{address}/{cellphone}/{postalAddress}/{emailAddress}/{nationalId}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long voterId = Long.parseLong(RestletUtil.getParameter(request, "voter"));
                    String fullnames = RestletUtil.getParameter(request, "fullnames");
                    String nationalId = RestletUtil.getParameter(request, "nationalId");
                    String address = RestletUtil.getParameter(request, "address");
                    String cellphone = RestletUtil.getParameter(request, "cellphone");
                    String postalAddress = RestletUtil.getParameter(request, "postalAddress");
                    String emailAddress = RestletUtil.getParameter(request, "emailAddress");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Voter voter = JPAFactory.getInstance().getVoterJpaController().findVoter(voterId);
                            voter.setFullnames(fullnames);
                            voter.setNationalId(nationalId);
                            voter.setAddress(address);
                            voter.setCellphone(cellphone);
                            voter.setPostalAddress(postalAddress);
                            voter.setEmailAddress(emailAddress);
                            JPAFactory.getInstance().getVoterJpaController().edit(voter);
                            response.setEntity(new JacksonRepresentation(new VoterData(voter)));
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

        router.attach("/deleteVoter/{session}/{voter}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long voterId = Long.parseLong(RestletUtil.getParameter(request, "voter"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            VoterRegistration registration = JPAFactory.getInstance().getVoterRegistrationJpaController().findVoterRegistrationByVoter(voterId);
                            JPAFactory.getInstance().getVoterRegistrationJpaController().destroy(registration.getId());

                            JPAFactory.getInstance().getVoterJpaController().destroy(voterId);
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

        router.attach("/getAllVoters/{session}/{max}/{index}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            int index = Integer.parseInt(RestletUtil.getParameter(request, "index"));
                            int max = Integer.parseInt(RestletUtil.getParameter(request, "max"));

                            List<Voter> voters = JPAFactory.getInstance().getVoterJpaController().findVoterEntities(max, index);

                            VoterData[] data = new VoterData[voters.size()];
                            int k = 0;
                            for (Voter v : voters) {
                                data[k] = new VoterData(v);
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

        router.attach("/getVoter/{session}/{voter}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long voterId = Long.parseLong(RestletUtil.getParameter(request, "voter"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Voter voter = JPAFactory.getInstance().getVoterJpaController().findVoter(voterId);
                            response.setEntity(new JacksonRepresentation(new VoterData(voter)));
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

        router.attach("/searchVoters/{searchText}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String searchText = RestletUtil.getParameter(request, "searchText");

                    List<Voter> voters = JPAFactory.getInstance().getVoterJpaController().findVoterByNationalId(searchText);

                    VoterData[] data = new VoterData[voters.size()];
                    int k = 0;
                    for (Voter v : voters) {
                        data[k] = new VoterData(v);
                        k++;
                    }

                    response.setEntity(new JacksonRepresentation(data));
                    response.setStatus(Status.SUCCESS_OK);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        router.attach("/addVoterRegistration/{session}/{voter}/{election}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
//private Voter voter;
//    private Election election;
                    String session = RestletUtil.getParameter(request, "session");
                    Long voterId = Long.parseLong(RestletUtil.getParameter(request, "voter"));
                    Long electionId = Long.parseLong(RestletUtil.getParameter(request, "election"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Voter voter = JPAFactory.getInstance().getVoterJpaController().findVoter(voterId);
                            Election election = JPAFactory.getInstance().getElectionJpaController().findElection(electionId);

                            VoterRegistration registration = new VoterRegistration();
                            registration.setVoter(voter);
                            registration.setElection(election);
                            JPAFactory.getInstance().getVoterRegistrationJpaController().create(registration);
                            response.setEntity(new JacksonRepresentation(new VoterRegistrationData(registration)));
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

        router.attach("/updateVoterRegistration/{session}/{voterRegistration}/{voter}/{election}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long registrationId = Long.parseLong(RestletUtil.getParameter(request, "voterRegistration"));
                    Long voterId = Long.parseLong(RestletUtil.getParameter(request, "voter"));
                    Long electionId = Long.parseLong(RestletUtil.getParameter(request, "election"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            Voter voter = JPAFactory.getInstance().getVoterJpaController().findVoter(voterId);
                            Election election = JPAFactory.getInstance().getElectionJpaController().findElection(electionId);

                            VoterRegistration registration = JPAFactory.getInstance().getVoterRegistrationJpaController().findVoterRegistration(registrationId);
                            registration.setVoter(voter);
                            registration.setElection(election);
                            JPAFactory.getInstance().getVoterRegistrationJpaController().edit(registration);
                            response.setEntity(new JacksonRepresentation(new VoterRegistrationData(registration)));
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

        router.attach("/deleteVoterRegistration/{session}/{voterRegistration}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long registrationId = Long.parseLong(RestletUtil.getParameter(request, "voterRegistration"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            JPAFactory.getInstance().getVoterRegistrationJpaController().destroy(registrationId);
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

        router.attach("/getAllVoterRegistrations/{session}/{max}/{index}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            int index = Integer.parseInt(RestletUtil.getParameter(request, "index"));
                            int max = Integer.parseInt(RestletUtil.getParameter(request, "max"));

                            List<VoterRegistration> registrations = JPAFactory.getInstance().getVoterRegistrationJpaController().findVoterRegistrationEntities(max, index);

                            VoterRegistrationData[] data = new VoterRegistrationData[registrations.size()];
                            int k = 0;
                            for (VoterRegistration vr : registrations) {
                                data[k] = new VoterRegistrationData(vr);
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

        router.attach("/getVoterRegistration/{session}/{voterRegistration}", new Restlet() {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String session = RestletUtil.getParameter(request, "session");
                    Long registrationId = Long.parseLong(RestletUtil.getParameter(request, "voterRegistration"));

                    LoginSession loginSession = Cache.getInstance().getSession(session);
                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
                        try {
                            VoterRegistration registration = JPAFactory.getInstance().getVoterRegistrationJpaController().findVoterRegistration(registrationId);
                            response.setEntity(new JacksonRepresentation(new VoterRegistrationData(registration)));
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

        //TODO: add calculate winner logic
//        router.attach("/calculateWinner/{session}/{election}", new Restlet() {
//
//            @Override
//            public void handle(Request request, Response response) {
//                try {
//                    String session = RestletUtil.getParameter(request, "session");
//                    Long electionId = Long.parseLong(RestletUtil.getParameter(request, "election"));
//
//                    LoginSession loginSession = Cache.getInstance().getSession(session);
//                    if (loginSession != null && loginSession.getUser().getUserRole() == UserRole.ADMINISTRATOR) {
//                        try {
//                            Election election = JPAFactory.getInstance().getElectionJpaController().findElection(electionId);
//                            
//                            
//                            response.setEntity(new JacksonRepresentation(election));
//                            response.setStatus(Status.SUCCESS_OK);
//                        } catch (Exception e) {
//                            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//                        }
//                    }
//                } catch (UnsupportedEncodingException ex) {
//                    Logger.getLogger(ApiApplication.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });
        return router;
    }
}
