package com.epam.esm.webservice.service.impl;

import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.repository.TagRepository;
import com.epam.esm.webservice.service.TagService;
import com.epam.esm.webservice.service.exception.TagNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private static final Logger logger = LogManager.getLogger();

    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public void save(Tag tag) {
        logger.debug("Creating new tag");
        tagRepository.save(tag);
        logger.debug("New tag created");
    }

    @Override
    public void delete(int tagId) {
        logger.debug("Deleting tag with id={}", tagId);
        tagRepository.delete(tagId);
        logger.debug("Tag deleted");
    }

    @Override
    public List<Tag> getAll() {
        return tagRepository.getAll();
    }

    @Override
    public Tag getById(int tagId) {
        return tagRepository.getById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
    }
}
