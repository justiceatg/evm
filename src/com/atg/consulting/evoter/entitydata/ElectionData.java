/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.entitydata;

import com.atg.consulting.evoter.domain.Election;
import com.atg.consulting.evoter.domain.Election.ElectionType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author brian
 */
public class ElectionData {

    static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Long id;
    private ConstituencyData constituency;
    private String startDate;
    private String endDate;
    private ElectionType electionType;

    public ElectionData(Election e) {
        id = e.getId();
        constituency = new ConstituencyData(e.getConstituency());
        startDate = dateFormat.format(e.getStartDate());
        endDate = dateFormat.format(e.getEndDate());
        electionType = e.getElectionType();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConstituencyData getConstituency() {
        return constituency;
    }

    public void setConstituency(ConstituencyData constituency) {
        this.constituency = constituency;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public ElectionType getElectionType() {
        return electionType;
    }

    public void setElectionType(ElectionType electionType) {
        this.electionType = electionType;
    }

}
