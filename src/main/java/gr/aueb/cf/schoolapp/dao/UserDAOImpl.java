package gr.aueb.cf.schoolapp.dao;

import gr.aueb.cf.schoolapp.model.User;
import gr.aueb.cf.schoolapp.service.util.JPAHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.ext.Provider;
import java.util.List;
@Provider
@RequestScoped
public class UserDAOImpl implements IUserDAO{
    @Override
    public User insert(User user) {
        EntityManager em = getEntityManager();
        em.persist(user);
        return user;
    }

    @Override
    public User update(User user) {
        getEntityManager().merge(user);
        return user;
    }

    @Override
    public void delete(Long id) {
        EntityManager em = getEntityManager();
        User userDelete = em.find(User.class, id);
        em.remove(userDelete);
    }

    @Override
    public List<User> getByUsername(String username) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<User> userCriteriaQuery = builder.createQuery(User.class);
        Root<User> root = userCriteriaQuery.from(User.class);

        ParameterExpression<String> tUsername = builder.parameter(String.class);
        userCriteriaQuery.select(root).where(builder.like(root.get("username"),tUsername));

        TypedQuery<User> query = getEntityManager().createQuery(userCriteriaQuery);
        query.setParameter(tUsername, username + "%");
        return query.getResultList();
    }

    @Override
    public User getById(Long id) {
        EntityManager em = getEntityManager();
        return em.find(User.class, id);
    }

    private EntityManager getEntityManager(){
        return JPAHelper.getEntityManager();
    }
}
