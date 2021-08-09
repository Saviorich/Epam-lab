package com.epam.esm.webservice.repository.impl;

import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.repository.CertificateRepository;
import com.epam.esm.webservice.util.CertificateParameters;
import com.epam.esm.webservice.util.Pagination;
import com.epam.esm.webservice.util.SortType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.webservice.util.SortType.ASC;
import static java.lang.Integer.MAX_VALUE;
import static java.util.Arrays.asList;

@Repository
public class CertificateRepositoryImpl implements CertificateRepository {

    private static final String ID_FIELD = "id";
    private static final String NAME_FIELD = "name";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String TAGS_FIELD = "tags";

    private static final String COUNT_ID_QUERY = "SELECT COUNT(id) FROM Certificate";

    private final SessionFactory sessionFactory;

    @Autowired
    public CertificateRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Certificate> findAllByParameters(CertificateParameters certificateParameters) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Certificate> criteria = createCriteria(certificateParameters, builder);
            int limit = certificateParameters.getPagination().getLimit();
            int offset = (certificateParameters.getPagination().getPage() - 1) * limit;
            return session.createQuery(criteria)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }

    @Override
    public Optional<Certificate> findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Certificate.class, id));
        }
    }

    @Override
    @Transactional
    public void add(Certificate certificate) {
        Session session = sessionFactory.getCurrentSession();
        session.save(certificate);
        session.flush();
    }

    @Override
    @Transactional
    public void update(Certificate certificate) {
        Session session = sessionFactory.getCurrentSession();
        session.update(certificate);
        session.flush();
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.delete(currentSession.load(Certificate.class, id));
        currentSession.flush();
    }

    @Override
    public Integer countFilteredEntries(CertificateParameters parameters) {
        if (parameters.getTags() == null && parameters.getSearch().trim().isEmpty()) {
            return countAllEntries();
        }
        return findAllByParameters(new CertificateParameters(
                parameters.getTags(),
                parameters.getSort(),
                parameters.getType(),
                parameters.getSearch(),
                new Pagination(1, MAX_VALUE)
        )).size();
    }

    private int countAllEntries() {
        try (Session session = sessionFactory.openSession()) {
            return ((Long) session.createQuery(COUNT_ID_QUERY)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .get())
                    .intValue();
        }
    }

    private CriteriaQuery<Certificate> createCriteria(CertificateParameters certificateParameters, CriteriaBuilder builder) {
        CriteriaQuery<Certificate> criteria = builder.createQuery(Certificate.class);
        Root<Certificate> root = criteria.from(Certificate.class);
        Predicate tagsPredicate = builder.conjunction(); // mocked predicate in case tags are null
        if (certificateParameters.getTags() != null && !certificateParameters.getTags().isEmpty()) {
            tagsPredicate = createTagsSearchPredicate(builder, criteria, root, certificateParameters.getTags());
        }
        String toSearch = "%" + certificateParameters.getSearch() + "%";
        Predicate searchPredicate = createSubstringSearchPredicate(builder, root, toSearch);
        addOrdering(builder, criteria, root, certificateParameters);
        criteria.where(builder.and(tagsPredicate, searchPredicate));
        return criteria;
    }

    private void addOrdering(CriteriaBuilder builder,
                             CriteriaQuery<Certificate> criteria,
                             Root<Certificate> root,
                             CertificateParameters certificateParameters) {
        String sortField = certificateParameters.getSort().toString();
        SortType type = certificateParameters.getType();
        Path<Object> path = root.get(sortField);
        Order order = type == ASC ? builder.asc(path) : builder.desc(path);
        criteria.orderBy(order);
    }

    private Predicate createSubstringSearchPredicate(CriteriaBuilder builder,
                                                     Root<Certificate> root,
                                                     String toSearch) {
        Predicate name = builder.like(root.get(NAME_FIELD), toSearch);
        Predicate description = builder.like(root.get(DESCRIPTION_FIELD), toSearch);
        return builder.or(name, description);
    }

    private Predicate createTagsSearchPredicate(CriteriaBuilder builder,
                                                CriteriaQuery<Certificate> criteria,
                                                Root<Certificate> root,
                                                List<String> tags) {
        Join<Certificate, Tag> join = root.join(TAGS_FIELD);
        criteria.groupBy(asList(root.get(ID_FIELD), root.get(NAME_FIELD)));
        criteria.having(builder.equal(builder.count(join.get(ID_FIELD)), tags.size()));
        return join.get(NAME_FIELD).in(tags);
    }
}
