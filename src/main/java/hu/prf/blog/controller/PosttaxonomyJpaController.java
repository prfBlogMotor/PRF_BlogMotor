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
import hu.prf.blog.entity.Taxonomy;
import hu.prf.blog.entity.Post;
import hu.prf.blog.entity.Posttaxonomy;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Bali
 */
public class PosttaxonomyJpaController implements Serializable {

    public PosttaxonomyJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Posttaxonomy posttaxonomy) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Taxonomy taxonomyid = posttaxonomy.getTaxonomyid();
            if (taxonomyid != null) {
                taxonomyid = em.getReference(taxonomyid.getClass(), taxonomyid.getId());
                posttaxonomy.setTaxonomyid(taxonomyid);
            }
            Post postid = posttaxonomy.getPostid();
            if (postid != null) {
                postid = em.getReference(postid.getClass(), postid.getId());
                posttaxonomy.setPostid(postid);
            }
            em.persist(posttaxonomy);
            if (taxonomyid != null) {
                taxonomyid.getPosttaxonomyCollection().add(posttaxonomy);
                taxonomyid = em.merge(taxonomyid);
            }
            if (postid != null) {
                postid.getPosttaxonomyCollection().add(posttaxonomy);
                postid = em.merge(postid);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPosttaxonomy(posttaxonomy.getId()) != null) {
                throw new PreexistingEntityException("Posttaxonomy " + posttaxonomy + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Posttaxonomy posttaxonomy) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Posttaxonomy persistentPosttaxonomy = em.find(Posttaxonomy.class, posttaxonomy.getId());
            Taxonomy taxonomyidOld = persistentPosttaxonomy.getTaxonomyid();
            Taxonomy taxonomyidNew = posttaxonomy.getTaxonomyid();
            Post postidOld = persistentPosttaxonomy.getPostid();
            Post postidNew = posttaxonomy.getPostid();
            if (taxonomyidNew != null) {
                taxonomyidNew = em.getReference(taxonomyidNew.getClass(), taxonomyidNew.getId());
                posttaxonomy.setTaxonomyid(taxonomyidNew);
            }
            if (postidNew != null) {
                postidNew = em.getReference(postidNew.getClass(), postidNew.getId());
                posttaxonomy.setPostid(postidNew);
            }
            posttaxonomy = em.merge(posttaxonomy);
            if (taxonomyidOld != null && !taxonomyidOld.equals(taxonomyidNew)) {
                taxonomyidOld.getPosttaxonomyCollection().remove(posttaxonomy);
                taxonomyidOld = em.merge(taxonomyidOld);
            }
            if (taxonomyidNew != null && !taxonomyidNew.equals(taxonomyidOld)) {
                taxonomyidNew.getPosttaxonomyCollection().add(posttaxonomy);
                taxonomyidNew = em.merge(taxonomyidNew);
            }
            if (postidOld != null && !postidOld.equals(postidNew)) {
                postidOld.getPosttaxonomyCollection().remove(posttaxonomy);
                postidOld = em.merge(postidOld);
            }
            if (postidNew != null && !postidNew.equals(postidOld)) {
                postidNew.getPosttaxonomyCollection().add(posttaxonomy);
                postidNew = em.merge(postidNew);
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
                Long id = posttaxonomy.getId();
                if (findPosttaxonomy(id) == null) {
                    throw new NonexistentEntityException("The posttaxonomy with id " + id + " no longer exists.");
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
            Posttaxonomy posttaxonomy;
            try {
                posttaxonomy = em.getReference(Posttaxonomy.class, id);
                posttaxonomy.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The posttaxonomy with id " + id + " no longer exists.", enfe);
            }
            Taxonomy taxonomyid = posttaxonomy.getTaxonomyid();
            if (taxonomyid != null) {
                taxonomyid.getPosttaxonomyCollection().remove(posttaxonomy);
                taxonomyid = em.merge(taxonomyid);
            }
            Post postid = posttaxonomy.getPostid();
            if (postid != null) {
                postid.getPosttaxonomyCollection().remove(posttaxonomy);
                postid = em.merge(postid);
            }
            em.remove(posttaxonomy);
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

    public List<Posttaxonomy> findPosttaxonomyEntities() {
        return findPosttaxonomyEntities(true, -1, -1);
    }

    public List<Posttaxonomy> findPosttaxonomyEntities(int maxResults, int firstResult) {
        return findPosttaxonomyEntities(false, maxResults, firstResult);
    }

    private List<Posttaxonomy> findPosttaxonomyEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Posttaxonomy.class));
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

    public Posttaxonomy findPosttaxonomy(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Posttaxonomy.class, id);
        } finally {
            em.close();
        }
    }

    public int getPosttaxonomyCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Posttaxonomy> rt = cq.from(Posttaxonomy.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
