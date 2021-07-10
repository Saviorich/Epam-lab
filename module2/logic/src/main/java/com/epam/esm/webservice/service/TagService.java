package com.epam.esm.webservice.service;

import com.epam.esm.webservice.entity.Tag;

import java.util.List;

public interface TagService {

    void save(Tag tag);

    void delete(int tagId);

    List<Tag> getAll();

    Tag getById(int tagId);
}
