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

    public static final String defaultUsername = "unknown user";
    public static final String defaultPassword = "!!!unknown!!!";

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
        if (results.size() != 1) {
            return null;
        }
        return (User) results.get(0);
    }

    public User getUnknownUser() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<User> u = cq.from(User.class);
        cq.select(u);
        cq.where(
                cb.and(
                        cb.equal(u.get("username"), defaultUsername),
                        cb.equal(u.get("password"), defaultPassword)
                )
        );

        List results = em.createQuery(cq).getResultList();
        if (results.size() != 1) {
            User user = new User();
            user.setUsername(defaultUsername);
            user.setPassword(defaultPassword);
            create(user);
            return getUnknownUser();
        }
        return (User) results.get(0);
    }
    
    public static boolean isDefaultUser(User u) {
        if (u == null) return false;
        return u.getUsername().equals(defaultUsername) && u.getPassword().equals(defaultPassword);
    }
}
