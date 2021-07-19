package com.epam.esm.webservice.repository.impl;

import com.epam.esm.webservice.entity.Order;
import com.epam.esm.webservice.repository.OrderRepository;
import com.epam.esm.webservice.util.Pagination;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private static final String ID_FIELD = "id";
    private static final String USER_FIELD = "user";
    private static final String COUNT_ID_QUERY = "select count(id) from Order where user.id=%d";

    private final SessionFactory sessionFactory;

    @Autowired
    public OrderRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Order> findAllByParameters(Pagination pagination) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
            criteria.from(Order.class);
            int limit = pagination.getLimit();
            int offset = (pagination.getPage() - 1) * limit;
            return session.createQuery(criteria)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }

    @Override
    public List<Order> findAllByUserId(Integer userId, Pagination pagination) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
            Root<Order> root = criteria.from(Order.class);
            criteria.where(builder.equal(root.get(USER_FIELD).get(ID_FIELD), userId));
            int limit = pagination.getLimit();
            int offset = (pagination.getPage() - 1) * limit;
            return session.createQuery(criteria)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }

    @Override
    public Optional<Order> findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
            Root<Order> root = criteria.from(Order.class);
            criteria.where(builder.equal(root.get(ID_FIELD), id));
            return session.createQuery(criteria)
                            .getResultList()
                            .stream()
                            .findFirst();
        }
    }

    @Override
    @Transactional
    public void saveOrder(Order order) {
        Session session = sessionFactory.getCurrentSession();
        session.save(order);
        session.flush();
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(session.load(Order.class, id));
        session.flush();
    }

    @Override
    public Integer countTotalEntries() {
        try (Session session = sessionFactory.openSession()) {
            final CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<Order> root = criteria.from(Order.class);
            criteria.select(builder.count(root.get(ID_FIELD)));

            return session.createQuery(criteria)
                    .getSingleResult()
                    .intValue();
        }
    }

    @Override
    public Integer countUserOrders(Integer userId) {
        try (Session session = sessionFactory.openSession()) {
            return ((Long)session.createQuery(format(COUNT_ID_QUERY, userId))
                    .getSingleResult())
                    .intValue();
        }
    }
}
