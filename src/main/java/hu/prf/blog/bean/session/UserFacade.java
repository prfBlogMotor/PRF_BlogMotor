/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.prf.blog.bean.session;

import hu.prf.blog.entity.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

/**
 *
 * @author Bali
 */
@Stateless
public class UserFacade extends AbstractFacade<User> {

    @PersistenceContext(unitName = "hu.prf.blog_BlogMotor_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }

    public User GetUserIfAuthenticated(String username, String password) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<User> u = cq.from(User.class);
        cq.select(u);
        //ParameterExpression<String> un = 
        cq.where(
                cb.and(
                        cb.equal(u.get("username"), username),
                        cb.equal(u.get("password"), password)
                )
        );

        List results = em.createQuery(cq).getResultList();
        if (results.size() != 1)
            return null;
        return (User)results.get(0);

    }
}
