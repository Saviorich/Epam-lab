package com.epam.esm.webservice.controller.impl;

import com.epam.esm.webservice.controller.PageableResourceController;
import com.epam.esm.webservice.dto.TagDTO;
import com.epam.esm.webservice.service.TagService;
import com.epam.esm.webservice.util.Pagination;
import com.epam.esm.webservice.util.ResponseEntityStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.epam.esm.webservice.util.PaginationLinkBuilder.buildPaginationLinks;

@Log4j2
@RestController
@RequestMapping("/tags")
public class TagController implements PageableResourceController<TagDTO> {

    private static final ResponseEntityStatus OK = new ResponseEntityStatus(HttpStatus.OK);

    private final TagService service;

    @Autowired
    public TagController(TagService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<CollectionModel<TagDTO>> findAll(@RequestParam(required = false, defaultValue = "1") Integer page,
                                                           @RequestParam(required = false, defaultValue = "3") Integer limit) {
        log.debug("GET request is processed in findAll method, pagination passed in request: page={}, limit={}", page, limit);
        Pagination pagination = new Pagination(page, limit);
        List<TagDTO> tags = service.findAllByParameters(pagination);
        return ResponseEntity.ok(CollectionModel.of(tags, buildPaginationLinks(service, getClass(), pagination)));
    }

    @GetMapping("/frequent")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TagDTO getFrequentTag() {
        log.debug("GET request is processed in getFrequentTag");
        return service.findMostUsedTagOfUserWithHighestCostOfAllOrders();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public TagDTO getById(@PathVariable Integer id) {
        log.debug("GET request is processed in getById method, id passed in request: {}", id);
        return service.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntityStatus saveTag(@RequestBody TagDTO tag) {
        log.debug("Tag passed in  POST request: {}", tag);

        service.save(tag);
        return OK;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntityStatus deleteTag(@PathVariable Integer id) {
        log.debug("Id passed in DELETE request: {}", id);
        service.deleteById(id);
        log.debug("Tag deleted.");
        return OK;
    }
}
