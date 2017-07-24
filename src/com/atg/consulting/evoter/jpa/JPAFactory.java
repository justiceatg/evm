/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.jpa;

import com.atg.consulting.evoter.Configuration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author justice
 */
public class JPAFactory {

    private static JPAFactory instance;
    protected EntityManagerFactory emf = null;
    private LoginSessionJpaController loginSessionJpaController;
    private UserJpaController userJpaController;
    private CandidateJpaController candidateJpaController;
    private ConstituencyJpaController constituencyJpaController;
    private ElectionJpaController electionJpaController;
    private ElectionOfficerJpaController electionOfficerJpaController;
    private GeneralElectionJpaController generalElectionJpaController;
    private VoteJpaController voteJpaController;
    private VoterJpaController voterJpaController;
    private VoterRegistrationJpaController voterRegistrationJpaController;

    private JPAFactory() {
        try {
            if (Configuration.getInstance().verifyDBProperties()) {
                Map<String, String> dbPropertiesMap = new HashMap<>();
                dbPropertiesMap.put("hibernate.connection.username", Configuration.getInstance().getJdbcUsername());
                dbPropertiesMap.put("hibernate.connection.driver_class", Configuration.getInstance().getJdbcDriver());
                dbPropertiesMap.put("hibernate.connection.password", Configuration.getInstance().getJdbcPassword());
                dbPropertiesMap.put("hibernate.connection.url", Configuration.getInstance().getJdbcUrl());
                dbPropertiesMap.put("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
                dbPropertiesMap.put("hibernate.hbm2ddl.auto", "update");
                emf = Persistence.createEntityManagerFactory("EVMProxyPU", dbPropertiesMap);

            } else {
                emf = Persistence.createEntityManagerFactory("EasyRatesPU");
            }
            loginSessionJpaController = new LoginSessionJpaController(emf);
            userJpaController = new UserJpaController(emf);
            candidateJpaController = new CandidateJpaController(emf);
            constituencyJpaController = new ConstituencyJpaController(emf);
            electionJpaController = new ElectionJpaController(emf);
            electionOfficerJpaController = new ElectionOfficerJpaController(emf);
            generalElectionJpaController = new GeneralElectionJpaController(emf);
            voteJpaController = new VoteJpaController(emf);
            voterJpaController = new VoterJpaController(emf);
            voterRegistrationJpaController = new VoterRegistrationJpaController(emf);

        } catch (Exception ex) {
            Logger.getLogger(JPAFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static JPAFactory getInstance() {
        if (instance == null) {
            instance = new JPAFactory();
        }
        return instance;
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    public UserJpaController getUserJpaController() {
        return userJpaController;
    }

    public LoginSessionJpaController getLoginSessionJpaController() {
        return loginSessionJpaController;
    }

    public CandidateJpaController getCandidateJpaController() {
        return candidateJpaController;
    }

    public ConstituencyJpaController getConstituencyJpaController() {
        return constituencyJpaController;
    }

    public ElectionJpaController getElectionJpaController() {
        return electionJpaController;
    }

    public ElectionOfficerJpaController getElectionOfficerJpaController() {
        return electionOfficerJpaController;
    }

    public GeneralElectionJpaController getGeneralElectionJpaController() {
        return generalElectionJpaController;
    }

    public VoteJpaController getVoteJpaController() {
        return voteJpaController;
    }

    public VoterJpaController getVoterJpaController() {
        return voterJpaController;
    }

    public VoterRegistrationJpaController getVoterRegistrationJpaController() {
        return voterRegistrationJpaController;
    }

}
