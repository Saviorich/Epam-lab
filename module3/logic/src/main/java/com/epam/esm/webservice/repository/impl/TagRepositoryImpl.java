package com.epam.esm.webservice.repository.impl;

import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Order;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.repository.TagRepository;
import com.epam.esm.webservice.util.Pagination;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class TagRepositoryImpl implements TagRepository {

    private static final String CERTIFICATES_FIELD = "certificates";
    private static final String ORDERS_FIELD = "orders";
    private static final String ID_FIELD = "id";
    private static final String USER_FIELD = "user";
    private static final String COST_FIELD = "cost";

    private final SessionFactory sessionFactory;

    @Autowired
    public TagRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Tag> findAllByParams(Pagination pagination) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Tag> criteria = builder.createQuery(Tag.class);
            criteria.from(Tag.class);
            int limit = pagination.getLimit();
            int offset = (pagination.getPage() - 1) * limit;
            return session.createQuery(criteria)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }

    @Override
    public Optional<Tag> findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(Tag.class, id));
        }
    }

    @Override
    @Transactional
    public Optional<Tag> findMostUsedTagOfUserWithHighestCostOfAllOrders() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Tag> tagCriteria = builder.createQuery(Tag.class);
            Root<Tag> tagRoot = tagCriteria.from(Tag.class);
            Join<Order, Certificate> orderJoin = tagRoot.join(CERTIFICATES_FIELD).join(ORDERS_FIELD);

            int userId = getIdOfUserWithHighestCostOfOrders(session, builder);
            tagCriteria.where(builder.equal(orderJoin.get(USER_FIELD).get(ID_FIELD), userId));
            tagCriteria.groupBy(orderJoin.get(USER_FIELD).get(ID_FIELD), tagRoot.get(ID_FIELD));
            tagCriteria.orderBy(builder.desc(builder.count(tagRoot.get(ID_FIELD))));

            return session.createQuery(tagCriteria)
                    .setMaxResults(1)
                    .stream()
                    .findFirst();
        }
    }

    private int getIdOfUserWithHighestCostOfOrders(Session session, CriteriaBuilder builder) {
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<Order> root = criteria.from(Order.class);
        criteria.groupBy(root.get(USER_FIELD).get(ID_FIELD));
        criteria.orderBy(builder.desc(builder.sum(root.get(COST_FIELD))));
        return session.createQuery(criteria).setMaxResults(1).stream().findFirst().get().getUser().getId();
    }

    @Override
    @Transactional
    public void add(Tag tag) {
        Session session = sessionFactory.getCurrentSession();
        session.save(tag);
        session.flush();
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(session.get(Tag.class, id));
        session.flush();
    }

    @Override
    public Integer countTotalEntries() {
        try (Session session = sessionFactory.openSession()) {
            final CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<Tag> root = criteria.from(Tag.class);
            criteria.select(builder.count(root));

            return session.createQuery(criteria)
                    .getSingleResult()
                    .intValue();
        }
    }
}
