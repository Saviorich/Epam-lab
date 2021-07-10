package com.epam.esm.webservice.repository;

import com.epam.esm.webservice.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository {

    void save(Tag tag);

    void delete(int tagId);

    List<Tag> getAll();

    Optional<Tag> getById(int tagId);
}
