package com.atg.consulting.evoter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import org.apache.catalina.startup.Tomcat;

/**
 *
 * @author justicewilliam
 */
public class WebServer {

    private static Tomcat tomcat;

    public static void start() throws Exception {
        tomcat = new Tomcat();
        String webappDirLocation = Configuration.getInstance().getTomcatDir();
        tomcat.setPort(Configuration.getInstance().getTomcatPort());
        tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());

        tomcat.start();
        tomcat.getServer().await();
    }

}
