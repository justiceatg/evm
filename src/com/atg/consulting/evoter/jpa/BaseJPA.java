/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.jpa;

import javax.persistence.EntityManagerFactory;

/**
 *
 * @author justice
 */
public class BaseJPA {

    protected EntityManagerFactory emf;

    public BaseJPA(EntityManagerFactory emf) {
        this.emf = emf;
    }

}
