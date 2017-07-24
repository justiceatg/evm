/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author brian
 */
@Entity
public class Constituency implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private ConstituencyType constituencyType;

    public enum ConstituencyType {
        WARD,
        DISTRICT,
        COUNTY,
        PROVINCE,
        STATE,
        CITY,
        MUNICIPALITY,
        COUNTRY
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
