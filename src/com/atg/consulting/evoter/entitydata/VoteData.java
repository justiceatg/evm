/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.entitydata;

import com.atg.consulting.evoter.domain.Vote;

/**
 *
 * @author brian
 */
public class VoteData {

    private Long id;
    private ElectionData election;
    private VoterRegistrationData voterRegistration;
    private CandidateData voted;
    private String signature;
    private int voteNumber;
    private String randomVerifier;

    public VoteData(Vote v) {
        id = v.getId();
        election = new ElectionData(v.getElection());
        voterRegistration = new VoterRegistrationData(v.getVoterRegistration());
        voted = new CandidateData(v.getVoted());
        signature = v.getSignature();
        voteNumber = v.getVoteNumber();
        randomVerifier = v.getRandomVerifier();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ElectionData getElection() {
        return election;
    }

    public void setElection(ElectionData election) {
        this.election = election;
    }

    public VoterRegistrationData getVoterRegistration() {
        return voterRegistration;
    }

    public void setVoterRegistration(VoterRegistrationData voterRegistration) {
        this.voterRegistration = voterRegistration;
    }

    public CandidateData getVoted() {
        return voted;
    }

    public void setVoted(CandidateData voted) {
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
