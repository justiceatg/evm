package com.atg.consulting.evoter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.restlet.Request;

/**
 *
 * @author justicewilliam
 */
public class RestletUtil {

    public static String getParameter(Request request, String name) throws UnsupportedEncodingException {
        return URLDecoder.decode(String.valueOf(request.getAttributes().get(name)), "UTF-8");
    }

    public static DateFormat getDateFormatter() {
        DateFormat dateFormatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzzz");
        return dateFormatter;
    }
}
