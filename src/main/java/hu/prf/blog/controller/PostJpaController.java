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
import hu.prf.blog.entity.Posttaxonomy;
import java.util.ArrayList;
import java.util.Collection;
import hu.prf.blog.entity.Comment;
import hu.prf.blog.entity.Post;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Bali
 */
public class PostJpaController implements Serializable {

    public PostJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Post post) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (post.getPosttaxonomyCollection() == null) {
            post.setPosttaxonomyCollection(new ArrayList<Posttaxonomy>());
        }
        if (post.getCommentCollection() == null) {
            post.setCommentCollection(new ArrayList<Comment>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User userid = post.getUserid();
            if (userid != null) {
                userid = em.getReference(userid.getClass(), userid.getId());
                post.setUserid(userid);
            }
            Collection<Posttaxonomy> attachedPosttaxonomyCollection = new ArrayList<Posttaxonomy>();
            for (Posttaxonomy posttaxonomyCollectionPosttaxonomyToAttach : post.getPosttaxonomyCollection()) {
                posttaxonomyCollectionPosttaxonomyToAttach = em.getReference(posttaxonomyCollectionPosttaxonomyToAttach.getClass(), posttaxonomyCollectionPosttaxonomyToAttach.getId());
                attachedPosttaxonomyCollection.add(posttaxonomyCollectionPosttaxonomyToAttach);
            }
            post.setPosttaxonomyCollection(attachedPosttaxonomyCollection);
            Collection<Comment> attachedCommentCollection = new ArrayList<Comment>();
            for (Comment commentCollectionCommentToAttach : post.getCommentCollection()) {
                commentCollectionCommentToAttach = em.getReference(commentCollectionCommentToAttach.getClass(), commentCollectionCommentToAttach.getId());
                attachedCommentCollection.add(commentCollectionCommentToAttach);
            }
            post.setCommentCollection(attachedCommentCollection);
            em.persist(post);
            if (userid != null) {
                userid.getPostCollection().add(post);
                userid = em.merge(userid);
            }
            for (Posttaxonomy posttaxonomyCollectionPosttaxonomy : post.getPosttaxonomyCollection()) {
                Post oldPostidOfPosttaxonomyCollectionPosttaxonomy = posttaxonomyCollectionPosttaxonomy.getPostid();
                posttaxonomyCollectionPosttaxonomy.setPostid(post);
                posttaxonomyCollectionPosttaxonomy = em.merge(posttaxonomyCollectionPosttaxonomy);
                if (oldPostidOfPosttaxonomyCollectionPosttaxonomy != null) {
                    oldPostidOfPosttaxonomyCollectionPosttaxonomy.getPosttaxonomyCollection().remove(posttaxonomyCollectionPosttaxonomy);
                    oldPostidOfPosttaxonomyCollectionPosttaxonomy = em.merge(oldPostidOfPosttaxonomyCollectionPosttaxonomy);
                }
            }
            for (Comment commentCollectionComment : post.getCommentCollection()) {
                Post oldPostidOfCommentCollectionComment = commentCollectionComment.getPostid();
                commentCollectionComment.setPostid(post);
                commentCollectionComment = em.merge(commentCollectionComment);
                if (oldPostidOfCommentCollectionComment != null) {
                    oldPostidOfCommentCollectionComment.getCommentCollection().remove(commentCollectionComment);
                    oldPostidOfCommentCollectionComment = em.merge(oldPostidOfCommentCollectionComment);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPost(post.getId()) != null) {
                throw new PreexistingEntityException("Post " + post + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Post post) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Post persistentPost = em.find(Post.class, post.getId());
            User useridOld = persistentPost.getUserid();
            User useridNew = post.getUserid();
            Collection<Posttaxonomy> posttaxonomyCollectionOld = persistentPost.getPosttaxonomyCollection();
            Collection<Posttaxonomy> posttaxonomyCollectionNew = post.getPosttaxonomyCollection();
            Collection<Comment> commentCollectionOld = persistentPost.getCommentCollection();
            Collection<Comment> commentCollectionNew = post.getCommentCollection();
            if (useridNew != null) {
                useridNew = em.getReference(useridNew.getClass(), useridNew.getId());
                post.setUserid(useridNew);
            }
            Collection<Posttaxonomy> attachedPosttaxonomyCollectionNew = new ArrayList<Posttaxonomy>();
            for (Posttaxonomy posttaxonomyCollectionNewPosttaxonomyToAttach : posttaxonomyCollectionNew) {
                posttaxonomyCollectionNewPosttaxonomyToAttach = em.getReference(posttaxonomyCollectionNewPosttaxonomyToAttach.getClass(), posttaxonomyCollectionNewPosttaxonomyToAttach.getId());
                attachedPosttaxonomyCollectionNew.add(posttaxonomyCollectionNewPosttaxonomyToAttach);
            }
            posttaxonomyCollectionNew = attachedPosttaxonomyCollectionNew;
            post.setPosttaxonomyCollection(posttaxonomyCollectionNew);
            Collection<Comment> attachedCommentCollectionNew = new ArrayList<Comment>();
            for (Comment commentCollectionNewCommentToAttach : commentCollectionNew) {
                commentCollectionNewCommentToAttach = em.getReference(commentCollectionNewCommentToAttach.getClass(), commentCollectionNewCommentToAttach.getId());
                attachedCommentCollectionNew.add(commentCollectionNewCommentToAttach);
            }
            commentCollectionNew = attachedCommentCollectionNew;
            post.setCommentCollection(commentCollectionNew);
            post = em.merge(post);
            if (useridOld != null && !useridOld.equals(useridNew)) {
                useridOld.getPostCollection().remove(post);
                useridOld = em.merge(useridOld);
            }
            if (useridNew != null && !useridNew.equals(useridOld)) {
                useridNew.getPostCollection().add(post);
                useridNew = em.merge(useridNew);
            }
            for (Posttaxonomy posttaxonomyCollectionOldPosttaxonomy : posttaxonomyCollectionOld) {
                if (!posttaxonomyCollectionNew.contains(posttaxonomyCollectionOldPosttaxonomy)) {
                    posttaxonomyCollectionOldPosttaxonomy.setPostid(null);
                    posttaxonomyCollectionOldPosttaxonomy = em.merge(posttaxonomyCollectionOldPosttaxonomy);
                }
            }
            for (Posttaxonomy posttaxonomyCollectionNewPosttaxonomy : posttaxonomyCollectionNew) {
                if (!posttaxonomyCollectionOld.contains(posttaxonomyCollectionNewPosttaxonomy)) {
                    Post oldPostidOfPosttaxonomyCollectionNewPosttaxonomy = posttaxonomyCollectionNewPosttaxonomy.getPostid();
                    posttaxonomyCollectionNewPosttaxonomy.setPostid(post);
                    posttaxonomyCollectionNewPosttaxonomy = em.merge(posttaxonomyCollectionNewPosttaxonomy);
                    if (oldPostidOfPosttaxonomyCollectionNewPosttaxonomy != null && !oldPostidOfPosttaxonomyCollectionNewPosttaxonomy.equals(post)) {
                        oldPostidOfPosttaxonomyCollectionNewPosttaxonomy.getPosttaxonomyCollection().remove(posttaxonomyCollectionNewPosttaxonomy);
                        oldPostidOfPosttaxonomyCollectionNewPosttaxonomy = em.merge(oldPostidOfPosttaxonomyCollectionNewPosttaxonomy);
                    }
                }
            }
            for (Comment commentCollectionOldComment : commentCollectionOld) {
                if (!commentCollectionNew.contains(commentCollectionOldComment)) {
                    commentCollectionOldComment.setPostid(null);
                    commentCollectionOldComment = em.merge(commentCollectionOldComment);
                }
            }
            for (Comment commentCollectionNewComment : commentCollectionNew) {
                if (!commentCollectionOld.contains(commentCollectionNewComment)) {
                    Post oldPostidOfCommentCollectionNewComment = commentCollectionNewComment.getPostid();
                    commentCollectionNewComment.setPostid(post);
                    commentCollectionNewComment = em.merge(commentCollectionNewComment);
                    if (oldPostidOfCommentCollectionNewComment != null && !oldPostidOfCommentCollectionNewComment.equals(post)) {
                        oldPostidOfCommentCollectionNewComment.getCommentCollection().remove(commentCollectionNewComment);
                        oldPostidOfCommentCollectionNewComment = em.merge(oldPostidOfCommentCollectionNewComment);
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
                Long id = post.getId();
                if (findPost(id) == null) {
                    throw new NonexistentEntityException("The post with id " + id + " no longer exists.");
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
            Post post;
            try {
                post = em.getReference(Post.class, id);
                post.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The post with id " + id + " no longer exists.", enfe);
            }
            User userid = post.getUserid();
            if (userid != null) {
                userid.getPostCollection().remove(post);
                userid = em.merge(userid);
            }
            Collection<Posttaxonomy> posttaxonomyCollection = post.getPosttaxonomyCollection();
            for (Posttaxonomy posttaxonomyCollectionPosttaxonomy : posttaxonomyCollection) {
                posttaxonomyCollectionPosttaxonomy.setPostid(null);
                posttaxonomyCollectionPosttaxonomy = em.merge(posttaxonomyCollectionPosttaxonomy);
            }
            Collection<Comment> commentCollection = post.getCommentCollection();
            for (Comment commentCollectionComment : commentCollection) {
                commentCollectionComment.setPostid(null);
                commentCollectionComment = em.merge(commentCollectionComment);
            }
            em.remove(post);
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

    public List<Post> findPostEntities() {
        return findPostEntities(true, -1, -1);
    }

    public List<Post> findPostEntities(int maxResults, int firstResult) {
        return findPostEntities(false, maxResults, firstResult);
    }

    private List<Post> findPostEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Post.class));
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

    public Post findPost(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Post.class, id);
        } finally {
            em.close();
        }
    }

    public int getPostCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Post> rt = cq.from(Post.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
