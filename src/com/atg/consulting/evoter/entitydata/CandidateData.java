/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.entitydata;

import com.atg.consulting.evoter.domain.Candidate;

/**
 *
 * @author brian
 */
public class CandidateData {

    private Long id;
    private String fullnames;
    private String address;
    private String cellphone;
    private String postalAddress;
    private String emailAddress;
    private String nationalId;
    private ElectionData election;
    private int candidateNumber;
    private boolean winner;

    public CandidateData(Candidate c) {
        id = c.getId();
        fullnames = c.getFullnames();
        address = c.getAddress();
        cellphone = c.getCellphone();
        postalAddress = c.getPostalAddress();
        emailAddress = c.getEmailAddress();
        nationalId = c.getNationalId();
        election = new ElectionData(c.getElection());
        candidateNumber = c.getCandidateNumber();
        winner = c.isWinner();
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

    public int getCandidateNumber() {
        return candidateNumber;
    }

    public void setCandidateNumber(int candidateNumber) {
        this.candidateNumber = candidateNumber;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

}
