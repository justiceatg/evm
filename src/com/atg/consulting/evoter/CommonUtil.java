/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter;

import com.google.gson.Gson;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Random;
import sun.misc.BASE64Encoder;

/**
 *
 * @author justice
 */
public class CommonUtil {

    public static String getSHADigest(String rawString) throws Exception {

        MessageDigest md = null;
        md = MessageDigest.getInstance("SHA");
        md.update(rawString.getBytes("UTF-8"));

        byte raw[] = md.digest(); //step 4
        String hash = (new BASE64Encoder()).encode(raw); //step 5
        return hash;
    }

    public static String trimLeadingZeros(String source) {
        source = source.trim();
        for (int i = 0; i < source.length(); ++i) {
            char c = source.charAt(i);
            if (c != '0' && !Character.isSpaceChar(c)) {
                return source.substring(i);
            }
        }
        return source;
    }

    public static int genRandom5DigitNumber() {
        Random r = new Random(System.currentTimeMillis());
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }
    static Gson gson = new Gson();

    public static String formatDecimal(double number) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(number);
    }
}
