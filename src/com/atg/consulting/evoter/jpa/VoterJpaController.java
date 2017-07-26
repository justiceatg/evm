/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.jpa;

import com.atg.consulting.evoter.domain.Voter;
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
public class VoterJpaController extends BaseJPA {

    public VoterJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Voter voter) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(voter);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Voter voter) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            voter = em.merge(voter);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = voter.getId();
                if (findVoter(id) == null) {
                    throw new NonexistentEntityException("The voter with id " + id + " no longer exists.");
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
            Voter voter;
            try {
                voter = em.getReference(Voter.class, id);
                voter.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The voter with id " + id + " no longer exists.", enfe);
            }
            em.remove(voter);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Voter> findVoterEntities() {
        return findVoterEntities(true, -1, -1);
    }

    public List<Voter> findVoterEntities(int maxResults, int firstResult) {
        return findVoterEntities(false, maxResults, firstResult);
    }

    private List<Voter> findVoterEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Voter.class));
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

    public Voter findVoter(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Voter.class, id);
        } finally {
            em.close();
        }
    }

    public int getVoterCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Voter> rt = cq.from(Voter.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public Long createAndReturnId(Voter voter) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(voter);
            em.flush();
            long generatedId = voter.getId();
            em.getTransaction().commit();

            return generatedId;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

}
