/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.prf.blog.controller;

import hu.prf.blog.controller.exceptions.NonexistentEntityException;
import hu.prf.blog.controller.exceptions.PreexistingEntityException;
import hu.prf.blog.controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import hu.prf.blog.entity.User;
import hu.prf.blog.entity.Visiting;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Bali
 */
public class VisitingJpaController implements Serializable {

    public VisitingJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Visiting visiting) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User userid = visiting.getUserid();
            if (userid != null) {
                userid = em.getReference(userid.getClass(), userid.getId());
                visiting.setUserid(userid);
            }
            em.persist(visiting);
            if (userid != null) {
                userid.getVisitingCollection().add(visiting);
                userid = em.merge(userid);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findVisiting(visiting.getId()) != null) {
                throw new PreexistingEntityException("Visiting " + visiting + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Visiting visiting) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Visiting persistentVisiting = em.find(Visiting.class, visiting.getId());
            User useridOld = persistentVisiting.getUserid();
            User useridNew = visiting.getUserid();
            if (useridNew != null) {
                useridNew = em.getReference(useridNew.getClass(), useridNew.getId());
                visiting.setUserid(useridNew);
            }
            visiting = em.merge(visiting);
            if (useridOld != null && !useridOld.equals(useridNew)) {
                useridOld.getVisitingCollection().remove(visiting);
                useridOld = em.merge(useridOld);
            }
            if (useridNew != null && !useridNew.equals(useridOld)) {
                useridNew.getVisitingCollection().add(visiting);
                useridNew = em.merge(useridNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = visiting.getId();
                if (findVisiting(id) == null) {
                    throw new NonexistentEntityException("The visiting with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Visiting visiting;
            try {
                visiting = em.getReference(Visiting.class, id);
                visiting.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The visiting with id " + id + " no longer exists.", enfe);
            }
            User userid = visiting.getUserid();
            if (userid != null) {
                userid.getVisitingCollection().remove(visiting);
                userid = em.merge(userid);
            }
            em.remove(visiting);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Visiting> findVisitingEntities() {
        return findVisitingEntities(true, -1, -1);
    }

    public List<Visiting> findVisitingEntities(int maxResults, int firstResult) {
        return findVisitingEntities(false, maxResults, firstResult);
    }

    private List<Visiting> findVisitingEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Visiting.class));
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

    public Visiting findVisiting(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Visiting.class, id);
        } finally {
            em.close();
        }
    }

    public int getVisitingCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Visiting> rt = cq.from(Visiting.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
