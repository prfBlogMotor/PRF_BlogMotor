/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.prf.blog.bean.session;


import static hu.prf.blog.bean.session.UserFacade.defaultPassword;
import static hu.prf.blog.bean.session.UserFacade.defaultUsername;
import hu.prf.blog.entity.Editing;
import hu.prf.blog.entity.User;
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
public class EditingFacade extends AbstractFacade<Editing> {

    @PersistenceContext(unitName = "hu.prf.blog_BlogMotor_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EditingFacade() {
        super(Editing.class);
    }
    
    public List<Editing> findEditingByPost(long id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Editing> root = cq.from(Editing.class);
        cq.select(root);
        cq.where(cb.equal(root.get("postid"), id));

        List results = em.createQuery(cq).getResultList();
        for (Object e : results) {
            System.out.println("******** ****** Editing ID : " + ((Editing)e).getId());
        }
        return results;
    }
}
