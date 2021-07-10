package com.epam.esm.webservice.controller;

import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.service.TagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    private static final Logger logger = LogManager.getLogger();

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public List<Tag> getAllTags() {
        return tagService.getAll();
    }

    @GetMapping("/{id}")
    public Tag getTagById(@PathVariable int id) {
        logger.debug("Id obtained in GET request: {}", id);
        return tagService.getById(id);
    }

    @PostMapping
    public HttpStatus createTag(@RequestBody Tag tag) {
        tagService.save(tag);
        return HttpStatus.OK;
    }

    @DeleteMapping("/{id}")
    public HttpStatus deleteTag(@PathVariable("id") int id) {
        tagService.delete(id);
        return HttpStatus.OK;
    }
}
