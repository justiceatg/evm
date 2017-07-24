/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.entitydata;

import com.atg.consulting.evoter.domain.ElectionOfficer;

/**
 *
 * @author brian
 */
public class ElectionOfficerData {

    private Long id;
    private String fullnames;
    private String address;
    private String cellphone;
    private String postalAddress;
    private String emailAddress;
    private String nationalId;
    private ElectionData election;

    public ElectionOfficerData(ElectionOfficer eo) {
        id = eo.getId();
        fullnames = eo.getFullnames();
        address = eo.getAddress();
        cellphone = eo.getCellphone();
        postalAddress = eo.getPostalAddress();
        emailAddress = eo.getEmailAddress();
        nationalId = eo.getNationalId();
        election = new ElectionData(eo.getElection());
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

    public ElectionData getElection() {
        return election;
    }

    public void setElection(ElectionData election) {
        this.election = election;
    }

}
