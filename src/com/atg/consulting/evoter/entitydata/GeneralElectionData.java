/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.entitydata;

import com.atg.consulting.evoter.domain.GeneralElection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author brian
 */
public class GeneralElectionData {

    static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Long id;
    private String startDate;
    private String endDate;

    public GeneralElectionData(GeneralElection ge) {
        id = ge.getId();
        startDate = dateFormat.format(ge.getStartDate());
        endDate = dateFormat.format(ge.getEndDate());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

}
