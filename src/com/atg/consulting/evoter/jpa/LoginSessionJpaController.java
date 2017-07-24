/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.jpa;

import com.atg.consulting.evoter.domain.LoginSession;
import com.atg.consulting.evoter.jpa.exceptions.NonexistentEntityException;
import com.atg.consulting.evoter.jpa.exceptions.PreexistingEntityException;
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
public class LoginSessionJpaController extends BaseJPA {

    public LoginSessionJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(LoginSession loginSession) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(loginSession);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findLoginSession(loginSession.getId()) != null) {
                throw new PreexistingEntityException("LoginSession " + loginSession + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(LoginSession loginSession) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            loginSession = em.merge(loginSession);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = loginSession.getId();
                if (findLoginSession(id) == null) {
                    throw new NonexistentEntityException("The loginSession with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            LoginSession loginSession;
            try {
                loginSession = em.getReference(LoginSession.class, id);
                loginSession.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The loginSession with id " + id + " no longer exists.", enfe);
            }
            em.remove(loginSession);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<LoginSession> findLoginSessionEntities() {
        return findLoginSessionEntities(true, -1, -1);
    }

    public List<LoginSession> findLoginSessionEntities(int maxResults, int firstResult) {
        return findLoginSessionEntities(false, maxResults, firstResult);
    }

    private List<LoginSession> findLoginSessionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(LoginSession.class));
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

    public LoginSession findLoginSession(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(LoginSession.class, id);
        } finally {
            em.close();
        }
    }

    public int getLoginSessionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<LoginSession> rt = cq.from(LoginSession.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
