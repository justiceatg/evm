/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.jpa;

import com.atg.consulting.evoter.domain.VoterRegistration;
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
public class VoterRegistrationJpaController extends BaseJPA {

    public VoterRegistrationJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(VoterRegistration voterRegistration) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(voterRegistration);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(VoterRegistration voterRegistration) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            voterRegistration = em.merge(voterRegistration);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = voterRegistration.getId();
                if (findVoterRegistration(id) == null) {
                    throw new NonexistentEntityException("The voterRegistration with id " + id + " no longer exists.");
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
            VoterRegistration voterRegistration;
            try {
                voterRegistration = em.getReference(VoterRegistration.class, id);
                voterRegistration.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The voterRegistration with id " + id + " no longer exists.", enfe);
            }
            em.remove(voterRegistration);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<VoterRegistration> findVoterRegistrationEntities() {
        return findVoterRegistrationEntities(true, -1, -1);
    }

    public List<VoterRegistration> findVoterRegistrationEntities(int maxResults, int firstResult) {
        return findVoterRegistrationEntities(false, maxResults, firstResult);
    }

    private List<VoterRegistration> findVoterRegistrationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(VoterRegistration.class));
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

    public VoterRegistration findVoterRegistration(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(VoterRegistration.class, id);
        } finally {
            em.close();
        }
    }

    public int getVoterRegistrationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<VoterRegistration> rt = cq.from(VoterRegistration.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public VoterRegistration findVoterRegistrationByVoter(Long voterId) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from VoterRegistration as o where o.voter.id =:voterId)");
            q.setParameter("voterId", voterId);
            return (VoterRegistration) q.getSingleResult();
        } finally {
            em.close();
        }
    }

}
