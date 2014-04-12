/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.prf.blog.bean.session;

import hu.prf.blog.entity.Taxonomy;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    
}
