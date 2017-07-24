/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author brian
 */
@Entity
public class Vote implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Election election;
    @ManyToOne
    private VoterRegistration voterRegistration;
    @ManyToOne
    private Candidate voted;

    //Vote Information
    private String signature;
    private int voteNumber;
    private String randomVerifier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public VoterRegistration getVoterRegistration() {
        return voterRegistration;
    }

    public void setVoterRegistration(VoterRegistration voterRegistration) {
        this.voterRegistration = voterRegistration;
    }

    public Candidate getVoted() {
        return voted;
    }

    public void setVoted(Candidate voted) {
        this.voted = voted;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getVoteNumber() {
        return voteNumber;
    }

    public void setVoteNumber(int voteNumber) {
        this.voteNumber = voteNumber;
    }

    public String getRandomVerifier() {
        return randomVerifier;
    }

    public void setRandomVerifier(String randomVerifier) {
        this.randomVerifier = randomVerifier;
    }

}
