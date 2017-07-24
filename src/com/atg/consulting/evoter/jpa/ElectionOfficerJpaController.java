/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.jpa;

import com.atg.consulting.evoter.domain.ElectionOfficer;
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
public class ElectionOfficerJpaController extends BaseJPA {

    public ElectionOfficerJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ElectionOfficer electionOfficer) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(electionOfficer);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ElectionOfficer electionOfficer) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            electionOfficer = em.merge(electionOfficer);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = electionOfficer.getId();
                if (findElectionOfficer(id) == null) {
                    throw new NonexistentEntityException("The electionOfficer with id " + id + " no longer exists.");
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
            ElectionOfficer electionOfficer;
            try {
                electionOfficer = em.getReference(ElectionOfficer.class, id);
                electionOfficer.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The electionOfficer with id " + id + " no longer exists.", enfe);
            }
            em.remove(electionOfficer);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ElectionOfficer> findElectionOfficerEntities() {
        return findElectionOfficerEntities(true, -1, -1);
    }

    public List<ElectionOfficer> findElectionOfficerEntities(int maxResults, int firstResult) {
        return findElectionOfficerEntities(false, maxResults, firstResult);
    }

    private List<ElectionOfficer> findElectionOfficerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ElectionOfficer.class));
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

    public ElectionOfficer findElectionOfficer(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ElectionOfficer.class, id);
        } finally {
            em.close();
        }
    }

    public int getElectionOfficerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ElectionOfficer> rt = cq.from(ElectionOfficer.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
