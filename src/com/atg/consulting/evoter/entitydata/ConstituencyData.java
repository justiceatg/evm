/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.entitydata;

import com.atg.consulting.evoter.domain.Constituency;
import com.atg.consulting.evoter.domain.Constituency.ConstituencyType;

/**
 *
 * @author brian
 */
public class ConstituencyData {

    private Long id;
    private String name;
    private ConstituencyType constituencyType;

    public ConstituencyData(Constituency c) {
        id = c.getId();
        name = c.getName();
        constituencyType = c.getConstituencyType();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConstituencyType getConstituencyType() {
        return constituencyType;
    }

    public void setConstituencyType(ConstituencyType constituencyType) {
        this.constituencyType = constituencyType;
    }

}
