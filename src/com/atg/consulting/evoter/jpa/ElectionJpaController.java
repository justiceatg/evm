/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.jpa;

import com.atg.consulting.evoter.domain.Election;
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
public class ElectionJpaController extends BaseJPA {

    public ElectionJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Election election) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(election);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Election election) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            election = em.merge(election);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = election.getId();
                if (findElection(id) == null) {
                    throw new NonexistentEntityException("The election with id " + id + " no longer exists.");
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
            Election election;
            try {
                election = em.getReference(Election.class, id);
                election.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The election with id " + id + " no longer exists.", enfe);
            }
            em.remove(election);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Election> findElectionEntities() {
        return findElectionEntities(true, -1, -1);
    }

    public List<Election> findElectionEntities(int maxResults, int firstResult) {
        return findElectionEntities(false, maxResults, firstResult);
    }

    private List<Election> findElectionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Election.class));
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

    public Election findElection(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Election.class, id);
        } finally {
            em.close();
        }
    }

    public int getElectionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Election> rt = cq.from(Election.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
