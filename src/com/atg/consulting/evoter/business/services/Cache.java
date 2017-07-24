/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.business.services;

import com.atg.consulting.evoter.domain.LoginSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Justice
 */
public class Cache extends Thread {

    private static Cache instance;
    private Map<String, LoginSession> map;

    private Cache() {
        map = new HashMap<>();
    }

    public LoginSession getSession(String id) {
        if (map.get(id) != null) {
            map.get(id).setLastActive(new Date());
            return map.get(id);
        }
        return null;
    }

    public void addSession(LoginSession ls) {
        if (ls != null) {
            map.put(ls.getId(), ls);
        }
    }

    public static Cache getInstance() {
        if (instance == null) {
            instance = new Cache();
        }

        return instance;
    }

    @Override
    public void run() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -5);
        Date time = cal.getTime();
        List<String> toDelete = new ArrayList<>();
        while (true) {
            try {
                toDelete.clear();
                if (!map.isEmpty()) {
                    Iterator<String> iterator = map.keySet().iterator();

                    try {

                        String key = iterator.next();
                        if (map.get(key).getLastActive().before(time)) {
                            toDelete.add(key);
                        }

                        for (String s : toDelete) {
                            map.remove(s);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                sleep(300000);
            } catch (Exception ex) {
                Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void remove(String session) {
        map.remove(session);
    }

}
