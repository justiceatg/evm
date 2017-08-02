/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atg.consulting.evoter.jpa;

import com.atg.consulting.evoter.domain.Constituency;
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
public class ConstituencyJpaController extends BaseJPA {

    public ConstituencyJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Constituency constituency) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(constituency);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Constituency constituency) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            constituency = em.merge(constituency);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = constituency.getId();
                if (findConstituency(id) == null) {
                    throw new NonexistentEntityException("The constituency with id " + id + " no longer exists.");
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
            Constituency constituency;
            try {
                constituency = em.getReference(Constituency.class, id);
                constituency.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The constituency with id " + id + " no longer exists.", enfe);
            }
            em.remove(constituency);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Constituency> findConstituencyEntities() {
        return findConstituencyEntities(true, -1, -1);
    }

    public List<Constituency> findConstituencyEntities(int maxResults, int firstResult) {
        return findConstituencyEntities(false, maxResults, firstResult);
    }

    private List<Constituency> findConstituencyEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Constituency.class));
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

    public Constituency findConstituency(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Constituency.class, id);
        } finally {
            em.close();
        }
    }

    public int getConstituencyCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Constituency> rt = cq.from(Constituency.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public List<Constituency> findConstituenciesByName(String searchText) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Constituency as o where o.name like :searchText");
            q.setParameter("searchText", "%" + searchText + "%");
            return q.getResultList();
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
            return null;
        } finally {
            em.close();
        }
    }

}
