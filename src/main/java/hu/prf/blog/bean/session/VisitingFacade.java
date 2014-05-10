/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.prf.blog.bean.session;

import static hu.prf.blog.bean.session.UserFacade.defaultPassword;
import static hu.prf.blog.bean.session.UserFacade.defaultUsername;
import hu.prf.blog.entity.User;
import hu.prf.blog.entity.Visiting;
import hu.prf.blog.entity.Visiting_;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Bali
 */
@Stateless
public class VisitingFacade extends AbstractFacade<Visiting> {

    @PersistenceContext(unitName = "hu.prf.blog_BlogMotor_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VisitingFacade() {
        super(Visiting.class);
    }

    public List<Visiting> getVisitingsInActualMonth() {
        Calendar from = Calendar.getInstance();
        from.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        from.clear(Calendar.MINUTE);
        from.clear(Calendar.SECOND);
        from.clear(Calendar.MILLISECOND);
        from.set(Calendar.DAY_OF_MONTH, 1);

        Calendar to = Calendar.getInstance();
        to.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        to.clear(Calendar.MINUTE);
        to.clear(Calendar.SECOND);
        to.clear(Calendar.MILLISECOND);
        to.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Visiting> u = cq.from(Visiting.class);
        cq.select(u);
        
        cq.where(
                cb.and(
                        cb.lessThanOrEqualTo(u.<Date>get("date"), to.getTime())
                        //, cb.greaterThanOrEqualTo(u.<Date>get("date"), to.getTime())
                )
        );

        List results = em.createQuery(cq).getResultList();
        return results;
    }
    
    public List<Visiting> getVisitingsInActualMonth2(Calendar from, Calendar to) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Visiting.class);
        Root root = cq.from(Visiting.class);
        Predicate predicate_date = cb.between(root.get(Visiting_.date), from.getTime(), to.getTime());
        cq.where(predicate_date);
        TypedQuery tq = em.createQuery(cq);

        List results = tq.getResultList();
        return results;
    }
}
