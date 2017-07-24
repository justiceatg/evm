/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.entitydata;

import com.atg.consulting.evoter.domain.LoginSession;
import java.util.Date;

/**
 *
 * @author Justice
 */
public class LoginSessionData {

    private String id;
    private UserData user;
    private Date creationDate;
    private boolean active;

    public LoginSessionData(LoginSession ls) {
        id = ls.getId();
        user = new UserData(ls.getUser());
        creationDate = ls.getCreationDate();
        active = ls.isActive();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
