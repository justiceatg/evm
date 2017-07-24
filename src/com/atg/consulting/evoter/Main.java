package com.atg.consulting.evoter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.atg.consulting.evoter.business.services.Cache;
import com.atg.consulting.evoter.domain.User;
import com.atg.consulting.evoter.domain.UserRole;
import com.atg.consulting.evoter.jpa.JPAFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Justice
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
        JPAFactory.getInstance();
        if(JPAFactory.getInstance().getUserJpaController().getUserCount() == 0) {
            createAdminAccount();
        }
  
        Cache.getInstance().start();
        try {
            WebServer.start();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error failed to start the webserver", ex);
        }
    }
    private static void createAdminAccount() {
        try {
            User a = new User();
            a.setUsername("admin");
            a.setPassword(CommonUtil.getSHADigest("3v0t3r"));
            a.setFullnames("Administrator");
            a.setUserRole(UserRole.ADMINISTRATOR);
            JPAFactory.getInstance().getUserJpaController().create(a);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
