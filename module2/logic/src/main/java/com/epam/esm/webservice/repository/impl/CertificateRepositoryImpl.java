package com.epam.esm.webservice.repository.impl;

import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.repository.CertificateRepository;
import com.epam.esm.webservice.util.SortBy;
import com.epam.esm.webservice.util.SortType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CertificateRepositoryImpl implements CertificateRepository {

    private static final String INSERT_QUERY =
            "INSERT INTO gift_certificate (`name`, description, price, duration, " +
                    "create_date, last_update_date) " +
                    "VALUES (?, ?, ?, ?, NOW(), NOW())";
    private static final String UPDATE_QUERY =
            "UPDATE gift_certificate " +
                    "SET name=IFNULL(?, `name`), description=IFNULL(?,description), price=IFNULL(?, price), duration=CASE WHEN ?=0 THEN DURATION ELSE ? END, create_date=IFNULL(?, create_date), last_update_date=NOW() " +
                    "WHERE id=?";
    private static final String DELETE_QUERY =
            "DELETE FROM gift_certificate WHERE id=?";
    private static final String GET_BY_ID_QUERY =
            "SELECT *  FROM gift_certificate WHERE id=?;";
    private static final String INSERT_IGNORE_INTO_JUNCTION_TABLE =
            "INSERT IGNORE INTO gift_certificate_has_tag (gift_certificate_id, tag_id) " +
                    "VALUES (?, (SELECT id FROM tag WHERE name=? LIMIT 1));";
    private static final String DELETE_FROM_JUNCTION_TABLE =
            "DELETE FROM gift_certificate_has_tag WHERE gift_certificate_id=?";
    private static final String GET_TAGS_BY_CERTIFICATE =
            "SELECT  t.id, t.name FROM gift_certificate_has_tag " +
                    "INNER JOIN tag t on gift_certificate_has_tag.tag_id = t.id " +
                    "WHERE gift_certificate_id=?";
    private static final String CALL_TAG_PROCEDURE =
            "CALL tag_insert_if_not_exists(?)";
    private static final String DELETE_TAG_FROM_CERTIFICATE =
            "DELETE FROM gift_certificate_has_tag WHERE gift_certificate_id=? AND tag_id=?";
    private static final String GET_ALL_WITH_PARAMS_QUERY =
            "SELECT " +
                " gc.* " +
            "FROM " +
                " gift_certificate gc " +
            " %s " +
            "WHERE " +
                " %s " +
                " CONCAT(gc.name, ' ', description) LIKE CONCAT('%%', ?, '%%') " +
            "GROUP BY " +
                " gc.id, " +
                " gc.name " +
            " %s " +
            "ORDER BY gc.%s %s";

    private static final String WHERE_PART = " t.name IN ('%s') AND ";
    private static final String HAVING_PART = " HAVING COUNT(DISTINCT t.id) = %d ";
    private static final String JOIN_PART =
            "INNER JOIN " +
                    " gift_certificate_has_tag gcht ON gcht.gift_certificate_id = gc.id " +
                    "INNER JOIN " +
                    " tag t ON t.id = gcht.tag_id ";

    private static final String EMPTY = "";
    private static final String DELIMITER = "','";

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public CertificateRepositoryImpl(DataSource dataSource, PlatformTransactionManager transactionManager) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public List<Certificate> getAllWithParams(List<String> tags, SortBy sortBy, SortType sortType, String toSearch) {
        String query = String.format(GET_ALL_WITH_PARAMS_QUERY, EMPTY, EMPTY, EMPTY, sortBy, sortType);
        if (tags != null) {
            String wherePart = String.format(WHERE_PART, String.join(DELIMITER, tags));
            String havingPart = String.format(HAVING_PART, tags.size());
            query = String.format(GET_ALL_WITH_PARAMS_QUERY, JOIN_PART, wherePart, havingPart, sortBy, sortType);
        }
        List<Certificate> certificates = jdbcTemplate.query(query,
                ps -> ps.setString(1, toSearch),
                (rs, rowNum) -> createCertificate(rs));
        initCertificatesWithTags(certificates);
        return certificates;
    }

    @Override
    public void update(Certificate certificate) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            jdbcTemplate.update(UPDATE_QUERY, certificate.getName(), certificate.getDescription(),
                    certificate.getPrice(), certificate.getDuration(), certificate.getDuration(),
                    certificate.getCreateDate(), certificate.getId());
        });
    }

    @Override
    public void add(Certificate certificate) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, certificate.getName());
                ps.setString(2, certificate.getDescription());
                ps.setBigDecimal(3, certificate.getPrice());
                ps.setInt(4, certificate.getDuration());
                return ps;
            }, keyHolder);
            certificate.setId(keyHolder.getKey().intValue());
            addTagsToCertificate(certificate);
        });
    }

    @Override
    public void delete(int certificateId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            jdbcTemplate.update(DELETE_FROM_JUNCTION_TABLE, certificateId);
            jdbcTemplate.update(DELETE_QUERY, certificateId);
        });
    }

    @Override
    public Optional<Certificate> getById(int certificateId) {
        Optional<Certificate> certificate = Optional.ofNullable(jdbcTemplate.query(GET_BY_ID_QUERY,
                ps -> ps.setInt(1, certificateId),
                rs -> {
                    if (rs.next()) {
                        return createCertificate(rs);
                    }
                    return null;
        }));
        certificate.ifPresent(this::initCertificatesWithTags);
        return certificate;
    }

    @Override
    public void addTagsToCertificate(Certificate  certificate) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            certificate.getTags().forEach(t -> jdbcTemplate.update(CALL_TAG_PROCEDURE, t.getName()));
            jdbcTemplate.batchUpdate(INSERT_IGNORE_INTO_JUNCTION_TABLE, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, certificate.getId());
                    ps.setString(2, certificate.getTags().get(i).getName());
                }

                @Override
                public int getBatchSize() {
                    return certificate.getTags().size();
                }
            });
        });
    }

    @Override
    public void deleteTagsFromCertificate(int certificateId, List<Tag> tagsToDelete) {
        transactionTemplate.executeWithoutResult(
                transactionStatus -> tagsToDelete.forEach(t -> jdbcTemplate.update(DELETE_TAG_FROM_CERTIFICATE,
                        certificateId,
                        t.getId()))
        );
    }

    private Tag createTag(ResultSet rs) throws SQLException {
        return new Tag(rs.getInt(1), rs.getString(2));
    }

    private Certificate createCertificate(ResultSet rs) throws SQLException {
        Certificate certificate = new Certificate();
        certificate.setId(rs.getInt(1));
        certificate.setName(rs.getString(2));
        certificate.setDescription(rs.getString(3));
        certificate.setPrice(rs.getBigDecimal(4));
        certificate.setDuration(rs.getInt(5));
        certificate.setCreateDate(rs.getDate(6));
        certificate.setLastUpdateDate(rs.getDate(7));
        return certificate;
    }

    private void initCertificatesWithTags(List<Certificate> certificates) {
        initCertificatesWithTags(certificates.toArray(new Certificate[0]));
    }

    private void initCertificatesWithTags(Certificate... certificates) {
        for (Certificate certificate : certificates) {
            certificate.setTags(jdbcTemplate.query(GET_TAGS_BY_CERTIFICATE,
                    ps -> ps.setInt(1, certificate.getId()),
                    (rs, rowNum) -> createTag(rs)));
        }
    }
}
