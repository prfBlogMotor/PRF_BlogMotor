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
import hu.prf.blog.entity.Post;
import java.util.ArrayList;
import java.util.Collection;
import hu.prf.blog.entity.Visiting;
import hu.prf.blog.entity.Comment;
import hu.prf.blog.entity.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Bali
 */
public class UserJpaController implements Serializable {

    public UserJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (user.getPostCollection() == null) {
            user.setPostCollection(new ArrayList<Post>());
        }
        if (user.getVisitingCollection() == null) {
            user.setVisitingCollection(new ArrayList<Visiting>());
        }
        if (user.getCommentCollection() == null) {
            user.setCommentCollection(new ArrayList<Comment>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Post> attachedPostCollection = new ArrayList<Post>();
            for (Post postCollectionPostToAttach : user.getPostCollection()) {
                postCollectionPostToAttach = em.getReference(postCollectionPostToAttach.getClass(), postCollectionPostToAttach.getId());
                attachedPostCollection.add(postCollectionPostToAttach);
            }
            user.setPostCollection(attachedPostCollection);
            Collection<Visiting> attachedVisitingCollection = new ArrayList<Visiting>();
            for (Visiting visitingCollectionVisitingToAttach : user.getVisitingCollection()) {
                visitingCollectionVisitingToAttach = em.getReference(visitingCollectionVisitingToAttach.getClass(), visitingCollectionVisitingToAttach.getId());
                attachedVisitingCollection.add(visitingCollectionVisitingToAttach);
            }
            user.setVisitingCollection(attachedVisitingCollection);
            Collection<Comment> attachedCommentCollection = new ArrayList<Comment>();
            for (Comment commentCollectionCommentToAttach : user.getCommentCollection()) {
                commentCollectionCommentToAttach = em.getReference(commentCollectionCommentToAttach.getClass(), commentCollectionCommentToAttach.getId());
                attachedCommentCollection.add(commentCollectionCommentToAttach);
            }
            user.setCommentCollection(attachedCommentCollection);
            em.persist(user);
            for (Post postCollectionPost : user.getPostCollection()) {
                User oldUseridOfPostCollectionPost = postCollectionPost.getUserid();
                postCollectionPost.setUserid(user);
                postCollectionPost = em.merge(postCollectionPost);
                if (oldUseridOfPostCollectionPost != null) {
                    oldUseridOfPostCollectionPost.getPostCollection().remove(postCollectionPost);
                    oldUseridOfPostCollectionPost = em.merge(oldUseridOfPostCollectionPost);
                }
            }
            for (Visiting visitingCollectionVisiting : user.getVisitingCollection()) {
                User oldUseridOfVisitingCollectionVisiting = visitingCollectionVisiting.getUserid();
                visitingCollectionVisiting.setUserid(user);
                visitingCollectionVisiting = em.merge(visitingCollectionVisiting);
                if (oldUseridOfVisitingCollectionVisiting != null) {
                    oldUseridOfVisitingCollectionVisiting.getVisitingCollection().remove(visitingCollectionVisiting);
                    oldUseridOfVisitingCollectionVisiting = em.merge(oldUseridOfVisitingCollectionVisiting);
                }
            }
            for (Comment commentCollectionComment : user.getCommentCollection()) {
                User oldUseridOfCommentCollectionComment = commentCollectionComment.getUserid();
                commentCollectionComment.setUserid(user);
                commentCollectionComment = em.merge(commentCollectionComment);
                if (oldUseridOfCommentCollectionComment != null) {
                    oldUseridOfCommentCollectionComment.getCommentCollection().remove(commentCollectionComment);
                    oldUseridOfCommentCollectionComment = em.merge(oldUseridOfCommentCollectionComment);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findUser(user.getId()) != null) {
                throw new PreexistingEntityException("User " + user + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User persistentUser = em.find(User.class, user.getId());
            Collection<Post> postCollectionOld = persistentUser.getPostCollection();
            Collection<Post> postCollectionNew = user.getPostCollection();
            Collection<Visiting> visitingCollectionOld = persistentUser.getVisitingCollection();
            Collection<Visiting> visitingCollectionNew = user.getVisitingCollection();
            Collection<Comment> commentCollectionOld = persistentUser.getCommentCollection();
            Collection<Comment> commentCollectionNew = user.getCommentCollection();
            Collection<Post> attachedPostCollectionNew = new ArrayList<Post>();
            for (Post postCollectionNewPostToAttach : postCollectionNew) {
                postCollectionNewPostToAttach = em.getReference(postCollectionNewPostToAttach.getClass(), postCollectionNewPostToAttach.getId());
                attachedPostCollectionNew.add(postCollectionNewPostToAttach);
            }
            postCollectionNew = attachedPostCollectionNew;
            user.setPostCollection(postCollectionNew);
            Collection<Visiting> attachedVisitingCollectionNew = new ArrayList<Visiting>();
            for (Visiting visitingCollectionNewVisitingToAttach : visitingCollectionNew) {
                visitingCollectionNewVisitingToAttach = em.getReference(visitingCollectionNewVisitingToAttach.getClass(), visitingCollectionNewVisitingToAttach.getId());
                attachedVisitingCollectionNew.add(visitingCollectionNewVisitingToAttach);
            }
            visitingCollectionNew = attachedVisitingCollectionNew;
            user.setVisitingCollection(visitingCollectionNew);
            Collection<Comment> attachedCommentCollectionNew = new ArrayList<Comment>();
            for (Comment commentCollectionNewCommentToAttach : commentCollectionNew) {
                commentCollectionNewCommentToAttach = em.getReference(commentCollectionNewCommentToAttach.getClass(), commentCollectionNewCommentToAttach.getId());
                attachedCommentCollectionNew.add(commentCollectionNewCommentToAttach);
            }
            commentCollectionNew = attachedCommentCollectionNew;
            user.setCommentCollection(commentCollectionNew);
            user = em.merge(user);
            for (Post postCollectionOldPost : postCollectionOld) {
                if (!postCollectionNew.contains(postCollectionOldPost)) {
                    postCollectionOldPost.setUserid(null);
                    postCollectionOldPost = em.merge(postCollectionOldPost);
                }
            }
            for (Post postCollectionNewPost : postCollectionNew) {
                if (!postCollectionOld.contains(postCollectionNewPost)) {
                    User oldUseridOfPostCollectionNewPost = postCollectionNewPost.getUserid();
                    postCollectionNewPost.setUserid(user);
                    postCollectionNewPost = em.merge(postCollectionNewPost);
                    if (oldUseridOfPostCollectionNewPost != null && !oldUseridOfPostCollectionNewPost.equals(user)) {
                        oldUseridOfPostCollectionNewPost.getPostCollection().remove(postCollectionNewPost);
                        oldUseridOfPostCollectionNewPost = em.merge(oldUseridOfPostCollectionNewPost);
                    }
                }
            }
            for (Visiting visitingCollectionOldVisiting : visitingCollectionOld) {
                if (!visitingCollectionNew.contains(visitingCollectionOldVisiting)) {
                    visitingCollectionOldVisiting.setUserid(null);
                    visitingCollectionOldVisiting = em.merge(visitingCollectionOldVisiting);
                }
            }
            for (Visiting visitingCollectionNewVisiting : visitingCollectionNew) {
                if (!visitingCollectionOld.contains(visitingCollectionNewVisiting)) {
                    User oldUseridOfVisitingCollectionNewVisiting = visitingCollectionNewVisiting.getUserid();
                    visitingCollectionNewVisiting.setUserid(user);
                    visitingCollectionNewVisiting = em.merge(visitingCollectionNewVisiting);
                    if (oldUseridOfVisitingCollectionNewVisiting != null && !oldUseridOfVisitingCollectionNewVisiting.equals(user)) {
                        oldUseridOfVisitingCollectionNewVisiting.getVisitingCollection().remove(visitingCollectionNewVisiting);
                        oldUseridOfVisitingCollectionNewVisiting = em.merge(oldUseridOfVisitingCollectionNewVisiting);
                    }
                }
            }
            for (Comment commentCollectionOldComment : commentCollectionOld) {
                if (!commentCollectionNew.contains(commentCollectionOldComment)) {
                    commentCollectionOldComment.setUserid(null);
                    commentCollectionOldComment = em.merge(commentCollectionOldComment);
                }
            }
            for (Comment commentCollectionNewComment : commentCollectionNew) {
                if (!commentCollectionOld.contains(commentCollectionNewComment)) {
                    User oldUseridOfCommentCollectionNewComment = commentCollectionNewComment.getUserid();
                    commentCollectionNewComment.setUserid(user);
                    commentCollectionNewComment = em.merge(commentCollectionNewComment);
                    if (oldUseridOfCommentCollectionNewComment != null && !oldUseridOfCommentCollectionNewComment.equals(user)) {
                        oldUseridOfCommentCollectionNewComment.getCommentCollection().remove(commentCollectionNewComment);
                        oldUseridOfCommentCollectionNewComment = em.merge(oldUseridOfCommentCollectionNewComment);
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
                Long id = user.getId();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
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
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            Collection<Post> postCollection = user.getPostCollection();
            for (Post postCollectionPost : postCollection) {
                postCollectionPost.setUserid(null);
                postCollectionPost = em.merge(postCollectionPost);
            }
            Collection<Visiting> visitingCollection = user.getVisitingCollection();
            for (Visiting visitingCollectionVisiting : visitingCollection) {
                visitingCollectionVisiting.setUserid(null);
                visitingCollectionVisiting = em.merge(visitingCollectionVisiting);
            }
            Collection<Comment> commentCollection = user.getCommentCollection();
            for (Comment commentCollectionComment : commentCollection) {
                commentCollectionComment.setUserid(null);
                commentCollectionComment = em.merge(commentCollectionComment);
            }
            em.remove(user);
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

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
