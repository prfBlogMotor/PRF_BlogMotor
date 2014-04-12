/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.prf.blog.controller;

import hu.prf.blog.controller.exceptions.NonexistentEntityException;
import hu.prf.blog.controller.exceptions.PreexistingEntityException;
import hu.prf.blog.controller.exceptions.RollbackFailureException;
import hu.prf.blog.entity.Comment;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import hu.prf.blog.entity.User;
import hu.prf.blog.entity.Post;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Bali
 */
public class CommentJpaController implements Serializable {

    public CommentJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Comment comment) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User userid = comment.getUserid();
            if (userid != null) {
                userid = em.getReference(userid.getClass(), userid.getId());
                comment.setUserid(userid);
            }
            Post postid = comment.getPostid();
            if (postid != null) {
                postid = em.getReference(postid.getClass(), postid.getId());
                comment.setPostid(postid);
            }
            em.persist(comment);
            if (userid != null) {
                userid.getCommentCollection().add(comment);
                userid = em.merge(userid);
            }
            if (postid != null) {
                postid.getCommentCollection().add(comment);
                postid = em.merge(postid);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findComment(comment.getId()) != null) {
                throw new PreexistingEntityException("Comment " + comment + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Comment comment) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Comment persistentComment = em.find(Comment.class, comment.getId());
            User useridOld = persistentComment.getUserid();
            User useridNew = comment.getUserid();
            Post postidOld = persistentComment.getPostid();
            Post postidNew = comment.getPostid();
            if (useridNew != null) {
                useridNew = em.getReference(useridNew.getClass(), useridNew.getId());
                comment.setUserid(useridNew);
            }
            if (postidNew != null) {
                postidNew = em.getReference(postidNew.getClass(), postidNew.getId());
                comment.setPostid(postidNew);
            }
            comment = em.merge(comment);
            if (useridOld != null && !useridOld.equals(useridNew)) {
                useridOld.getCommentCollection().remove(comment);
                useridOld = em.merge(useridOld);
            }
            if (useridNew != null && !useridNew.equals(useridOld)) {
                useridNew.getCommentCollection().add(comment);
                useridNew = em.merge(useridNew);
            }
            if (postidOld != null && !postidOld.equals(postidNew)) {
                postidOld.getCommentCollection().remove(comment);
                postidOld = em.merge(postidOld);
            }
            if (postidNew != null && !postidNew.equals(postidOld)) {
                postidNew.getCommentCollection().add(comment);
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
                Long id = comment.getId();
                if (findComment(id) == null) {
                    throw new NonexistentEntityException("The comment with id " + id + " no longer exists.");
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
            Comment comment;
            try {
                comment = em.getReference(Comment.class, id);
                comment.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The comment with id " + id + " no longer exists.", enfe);
            }
            User userid = comment.getUserid();
            if (userid != null) {
                userid.getCommentCollection().remove(comment);
                userid = em.merge(userid);
            }
            Post postid = comment.getPostid();
            if (postid != null) {
                postid.getCommentCollection().remove(comment);
                postid = em.merge(postid);
            }
            em.remove(comment);
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

    public List<Comment> findCommentEntities() {
        return findCommentEntities(true, -1, -1);
    }

    public List<Comment> findCommentEntities(int maxResults, int firstResult) {
        return findCommentEntities(false, maxResults, firstResult);
    }

    private List<Comment> findCommentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Comment.class));
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

    public Comment findComment(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Comment.class, id);
        } finally {
            em.close();
        }
    }

    public int getCommentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Comment> rt = cq.from(Comment.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
