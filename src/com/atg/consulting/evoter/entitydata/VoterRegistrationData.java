/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.entitydata;

import com.atg.consulting.evoter.domain.VoterRegistration;

/**
 *
 * @author brian
 */
public class VoterRegistrationData {

    private Long id;
    private VoterData voter;
    private ElectionData election;

    public VoterRegistrationData(VoterRegistration vr) {
        id = vr.getId();
        voter = new VoterData(vr.getVoter());
        election = new ElectionData(vr.getElection());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VoterData getVoter() {
        return voter;
    }

    public void setVoter(VoterData voter) {
        this.voter = voter;
    }

    public ElectionData getElection() {
        return election;
    }

    public void setElection(ElectionData election) {
        this.election = election;
    }

}
