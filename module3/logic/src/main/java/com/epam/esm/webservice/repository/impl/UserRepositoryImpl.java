package com.epam.esm.webservice.repository.impl;

import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.User;
import com.epam.esm.webservice.repository.UserRepository;
import com.epam.esm.webservice.util.CertificateParameters;
import com.epam.esm.webservice.util.Pagination;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<User> findAllByParameters(Pagination pagination) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
            CriteriaQuery<User> criteria = builder.createQuery(User.class);
            criteria.from(User.class);
            int limit = pagination.getLimit();
            int offset = (pagination.getPage() - 1) * limit;
            return session.createQuery(criteria)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }

    @Override
    public Optional<User> findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(User.class, id));
        }
    }

    @Override
    public Integer countTotalEntries() {
        try (Session session = sessionFactory.openSession()) {
            final CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<User> root = criteria.from(User.class);
            criteria.select(builder.count(root));

            return session.createQuery(criteria)
                    .getSingleResult()
                    .intValue();
        }
    }
}
