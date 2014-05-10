/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.prf.blog.bean.session;

import hu.prf.blog.entity.Post;
import hu.prf.blog.entity.Posttaxonomy;
import hu.prf.blog.entity.Taxonomy;
import hu.prf.blog.entity.User;
import java.util.Collection;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Bali
 */
@Stateless
public class PosttaxonomyFacade extends AbstractFacade<Posttaxonomy> {
    @PersistenceContext(unitName = "hu.prf.blog_BlogMotor_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PosttaxonomyFacade() {
        super(Posttaxonomy.class);
    }
    
    public Collection<Posttaxonomy> findAllPostTaxonomiesByTaxonomy(Taxonomy taxonomy) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Posttaxonomy> u = cq.from(Posttaxonomy.class);
        cq.select(u);
        cq.where(cb.equal(u.get("taxonomyid"), taxonomy.getId()));

        List results = em.createQuery(cq).getResultList();
        return results;
    }
    
    public Collection<Posttaxonomy> findAllPostTaxonomiesByPost(Post post) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Posttaxonomy> u = cq.from(Posttaxonomy.class);
        cq.select(u);
        cq.where(cb.equal(u.get("postid"), post.getId()));

        List results = em.createQuery(cq).getResultList();
        return results;
    }
}
