/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.entitydata;

import com.atg.consulting.evoter.domain.Voter;

/**
 *
 * @author brian
 */
public class VoterData {

    private Long id;
    private String fullnames;
    private String address;
    private String cellphone;
    private String postalAddress;
    private String emailAddress;
    private String nationalId;

    public VoterData(Voter v) {
        id = v.getId();
        fullnames = v.getFullnames();
        address = v.getAddress();
        cellphone = v.getCellphone();
        postalAddress = v.getPostalAddress();
        emailAddress = v.getEmailAddress();
        nationalId = v.getNationalId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullnames() {
        return fullnames;
    }

    public void setFullnames(String fullnames) {
        this.fullnames = fullnames;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

}
