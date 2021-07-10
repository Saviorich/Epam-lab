package com.epam.esm.webservice.repository.impl;

import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class TagRepositoryImpl implements TagRepository {

    private static final String  INSERT_QUERY = "INSERT INTO tag (name) VALUES (?)";
    private static final String DELETE_QUERY = "DELETE FROM tag WHERE id=?";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM tag WHERE id=?";
    private static final String GET_ALL_QUERY = "SELECT * FROM tag";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TagRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void save(Tag tag) {
        jdbcTemplate.update(INSERT_QUERY, tag.getName());
    }

    @Override
    public void delete(int tagId) {
        jdbcTemplate.update(DELETE_QUERY, tagId);
    }

    @Override
    public Optional<Tag> getById(int tagId) {
        return Optional.ofNullable(jdbcTemplate.query(GET_BY_ID_QUERY,
                ps -> ps.setInt(1, tagId),
                rs -> {
                    if (rs.next()) {
                        return createTag(rs);
                    }
                    return null;
                }));
    }

    @Override
    public List<Tag> getAll() {
        return jdbcTemplate.query(GET_ALL_QUERY,
                (rs, rowNum) -> createTag(rs));
    }

    private Tag createTag(ResultSet rs) throws SQLException {
        Tag tag = new Tag();
        tag.setId(rs.getInt(1));
        tag.setName(rs.getString(2));
        return tag;
    }
}
