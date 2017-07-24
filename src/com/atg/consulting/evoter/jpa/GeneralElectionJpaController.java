/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.jpa;

import com.atg.consulting.evoter.domain.GeneralElection;
import com.atg.consulting.evoter.jpa.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author brian
 */
public class GeneralElectionJpaController extends BaseJPA {

    public GeneralElectionJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(GeneralElection generalElection) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(generalElection);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(GeneralElection generalElection) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            generalElection = em.merge(generalElection);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = generalElection.getId();
                if (findGeneralElection(id) == null) {
                    throw new NonexistentEntityException("The generalElection with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            GeneralElection generalElection;
            try {
                generalElection = em.getReference(GeneralElection.class, id);
                generalElection.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The generalElection with id " + id + " no longer exists.", enfe);
            }
            em.remove(generalElection);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<GeneralElection> findGeneralElectionEntities() {
        return findGeneralElectionEntities(true, -1, -1);
    }

    public List<GeneralElection> findGeneralElectionEntities(int maxResults, int firstResult) {
        return findGeneralElectionEntities(false, maxResults, firstResult);
    }

    private List<GeneralElection> findGeneralElectionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(GeneralElection.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public GeneralElection findGeneralElection(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(GeneralElection.class, id);
        } finally {
            em.close();
        }
    }

    public int getGeneralElectionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<GeneralElection> rt = cq.from(GeneralElection.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
