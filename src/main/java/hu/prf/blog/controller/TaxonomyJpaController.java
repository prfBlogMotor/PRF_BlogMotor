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
import hu.prf.blog.entity.Posttaxonomy;
import hu.prf.blog.entity.Taxonomy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Bali
 */
public class TaxonomyJpaController implements Serializable {

    public TaxonomyJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Taxonomy taxonomy) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (taxonomy.getPosttaxonomyCollection() == null) {
            taxonomy.setPosttaxonomyCollection(new ArrayList<Posttaxonomy>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Posttaxonomy> attachedPosttaxonomyCollection = new ArrayList<Posttaxonomy>();
            for (Posttaxonomy posttaxonomyCollectionPosttaxonomyToAttach : taxonomy.getPosttaxonomyCollection()) {
                posttaxonomyCollectionPosttaxonomyToAttach = em.getReference(posttaxonomyCollectionPosttaxonomyToAttach.getClass(), posttaxonomyCollectionPosttaxonomyToAttach.getId());
                attachedPosttaxonomyCollection.add(posttaxonomyCollectionPosttaxonomyToAttach);
            }
            taxonomy.setPosttaxonomyCollection(attachedPosttaxonomyCollection);
            em.persist(taxonomy);
            for (Posttaxonomy posttaxonomyCollectionPosttaxonomy : taxonomy.getPosttaxonomyCollection()) {
                Taxonomy oldTaxonomyidOfPosttaxonomyCollectionPosttaxonomy = posttaxonomyCollectionPosttaxonomy.getTaxonomyid();
                posttaxonomyCollectionPosttaxonomy.setTaxonomyid(taxonomy);
                posttaxonomyCollectionPosttaxonomy = em.merge(posttaxonomyCollectionPosttaxonomy);
                if (oldTaxonomyidOfPosttaxonomyCollectionPosttaxonomy != null) {
                    oldTaxonomyidOfPosttaxonomyCollectionPosttaxonomy.getPosttaxonomyCollection().remove(posttaxonomyCollectionPosttaxonomy);
                    oldTaxonomyidOfPosttaxonomyCollectionPosttaxonomy = em.merge(oldTaxonomyidOfPosttaxonomyCollectionPosttaxonomy);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findTaxonomy(taxonomy.getId()) != null) {
                throw new PreexistingEntityException("Taxonomy " + taxonomy + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Taxonomy taxonomy) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Taxonomy persistentTaxonomy = em.find(Taxonomy.class, taxonomy.getId());
            Collection<Posttaxonomy> posttaxonomyCollectionOld = persistentTaxonomy.getPosttaxonomyCollection();
            Collection<Posttaxonomy> posttaxonomyCollectionNew = taxonomy.getPosttaxonomyCollection();
            Collection<Posttaxonomy> attachedPosttaxonomyCollectionNew = new ArrayList<Posttaxonomy>();
            for (Posttaxonomy posttaxonomyCollectionNewPosttaxonomyToAttach : posttaxonomyCollectionNew) {
                posttaxonomyCollectionNewPosttaxonomyToAttach = em.getReference(posttaxonomyCollectionNewPosttaxonomyToAttach.getClass(), posttaxonomyCollectionNewPosttaxonomyToAttach.getId());
                attachedPosttaxonomyCollectionNew.add(posttaxonomyCollectionNewPosttaxonomyToAttach);
            }
            posttaxonomyCollectionNew = attachedPosttaxonomyCollectionNew;
            taxonomy.setPosttaxonomyCollection(posttaxonomyCollectionNew);
            taxonomy = em.merge(taxonomy);
            for (Posttaxonomy posttaxonomyCollectionOldPosttaxonomy : posttaxonomyCollectionOld) {
                if (!posttaxonomyCollectionNew.contains(posttaxonomyCollectionOldPosttaxonomy)) {
                    posttaxonomyCollectionOldPosttaxonomy.setTaxonomyid(null);
                    posttaxonomyCollectionOldPosttaxonomy = em.merge(posttaxonomyCollectionOldPosttaxonomy);
                }
            }
            for (Posttaxonomy posttaxonomyCollectionNewPosttaxonomy : posttaxonomyCollectionNew) {
                if (!posttaxonomyCollectionOld.contains(posttaxonomyCollectionNewPosttaxonomy)) {
                    Taxonomy oldTaxonomyidOfPosttaxonomyCollectionNewPosttaxonomy = posttaxonomyCollectionNewPosttaxonomy.getTaxonomyid();
                    posttaxonomyCollectionNewPosttaxonomy.setTaxonomyid(taxonomy);
                    posttaxonomyCollectionNewPosttaxonomy = em.merge(posttaxonomyCollectionNewPosttaxonomy);
                    if (oldTaxonomyidOfPosttaxonomyCollectionNewPosttaxonomy != null && !oldTaxonomyidOfPosttaxonomyCollectionNewPosttaxonomy.equals(taxonomy)) {
                        oldTaxonomyidOfPosttaxonomyCollectionNewPosttaxonomy.getPosttaxonomyCollection().remove(posttaxonomyCollectionNewPosttaxonomy);
                        oldTaxonomyidOfPosttaxonomyCollectionNewPosttaxonomy = em.merge(oldTaxonomyidOfPosttaxonomyCollectionNewPosttaxonomy);
                    }
                }
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
                Long id = taxonomy.getId();
                if (findTaxonomy(id) == null) {
                    throw new NonexistentEntityException("The taxonomy with id " + id + " no longer exists.");
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
            Taxonomy taxonomy;
            try {
                taxonomy = em.getReference(Taxonomy.class, id);
                taxonomy.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The taxonomy with id " + id + " no longer exists.", enfe);
            }
            Collection<Posttaxonomy> posttaxonomyCollection = taxonomy.getPosttaxonomyCollection();
            for (Posttaxonomy posttaxonomyCollectionPosttaxonomy : posttaxonomyCollection) {
                posttaxonomyCollectionPosttaxonomy.setTaxonomyid(null);
                posttaxonomyCollectionPosttaxonomy = em.merge(posttaxonomyCollectionPosttaxonomy);
            }
            em.remove(taxonomy);
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

    public List<Taxonomy> findTaxonomyEntities() {
        return findTaxonomyEntities(true, -1, -1);
    }

    public List<Taxonomy> findTaxonomyEntities(int maxResults, int firstResult) {
        return findTaxonomyEntities(false, maxResults, firstResult);
    }

    private List<Taxonomy> findTaxonomyEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Taxonomy.class));
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

    public Taxonomy findTaxonomy(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Taxonomy.class, id);
        } finally {
            em.close();
        }
    }

    public int getTaxonomyCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Taxonomy> rt = cq.from(Taxonomy.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
