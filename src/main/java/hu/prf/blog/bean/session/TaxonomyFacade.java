/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.prf.blog.bean.session;

import hu.prf.blog.entity.Posttaxonomy;
import hu.prf.blog.entity.Taxonomy;
import java.util.Collection;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Bali
 */
@Stateless
public class TaxonomyFacade extends AbstractFacade<Taxonomy> {
    @PersistenceContext(unitName = "hu.prf.blog_BlogMotor_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TaxonomyFacade() {
        super(Taxonomy.class);
    }
    
    public Taxonomy findByCategoryName(String categoryName) {
        TypedQuery<Taxonomy> query =
            em.createNamedQuery("Taxonomy.findByCategoryname", Taxonomy.class);
        return query.setParameter("categoryname", categoryName).getSingleResult();
    }
    
    public Taxonomy findById(Long categoryId) {
        TypedQuery<Taxonomy> query =
            em.createNamedQuery("Taxonomy.findById", Taxonomy.class);
        return query.setParameter("id", categoryId).getSingleResult();
    }
}
