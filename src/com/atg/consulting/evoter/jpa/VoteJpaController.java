/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.jpa;

import com.atg.consulting.evoter.domain.Vote;
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
public class VoteJpaController extends BaseJPA {

    public VoteJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Vote vote) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(vote);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Vote vote) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            vote = em.merge(vote);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = vote.getId();
                if (findVote(id) == null) {
                    throw new NonexistentEntityException("The vote with id " + id + " no longer exists.");
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
            Vote vote;
            try {
                vote = em.getReference(Vote.class, id);
                vote.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vote with id " + id + " no longer exists.", enfe);
            }
            em.remove(vote);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Vote> findVoteEntities() {
        return findVoteEntities(true, -1, -1);
    }

    public List<Vote> findVoteEntities(int maxResults, int firstResult) {
        return findVoteEntities(false, maxResults, firstResult);
    }

    private List<Vote> findVoteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Vote.class));
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

    public Vote findVote(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Vote.class, id);
        } finally {
            em.close();
        }
    }

    public int getVoteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Vote> rt = cq.from(Vote.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
