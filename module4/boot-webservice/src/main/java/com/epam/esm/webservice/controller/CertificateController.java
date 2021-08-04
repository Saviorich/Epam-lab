package com.epam.esm.webservice.controller;

import com.epam.esm.webservice.dto.CertificateDTO;
import com.epam.esm.webservice.service.CertificateService;
import com.epam.esm.webservice.util.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.epam.esm.webservice.util.PaginationLinkBuilder.buildPaginationLinks;

@Log4j2
@RestController
@RequestMapping("/certificates")
public class CertificateController {

    private static final ResponseEntityStatus OK = new ResponseEntityStatus(HttpStatus.OK);

    private final CertificateService service;

    @Autowired
    public CertificateController(CertificateService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<CertificateDTO>> getAllWithParams(@RequestParam(required = false) List<String> tags,
                                                                            @RequestParam(required = false, defaultValue = "name") SortBy sort,
                                                                            @RequestParam(required = false, defaultValue = "asc") SortType type,
                                                                            @RequestParam(required = false, defaultValue = "") String search,
                                                                            @RequestParam(required = false, defaultValue = "1") Integer page,
                                                                            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        Pagination pagination = new Pagination(page, limit);
        CertificateParameters parameters = new CertificateParameters(tags, sort, type, search, pagination);
        log.debug("GET request is processed in getAllWithParams method, parameters passed in request: {}", parameters);
        List<CertificateDTO> certificates = service.findAllWithParams(parameters);
        int count = service.countFilteredCertificates(parameters);
        return ResponseEntity.ok(CollectionModel.of(certificates, buildPaginationLinks(count, parameters, pagination)));
    }

    @GetMapping("/{id}")
    public CertificateDTO getById(@PathVariable Integer id) {
        log.debug("GET request is processed in getById method, id passed in request: {}", id);
        return service.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntityStatus addCertificate(@RequestBody CertificateDTO certificate) {
        log.debug("Certificate passed in POST request: {}", certificate);
        service.save(certificate);
        return OK;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntityStatus updateCertificate(@RequestBody CertificateDTO certificate) {
        log.debug("Certificate passed in PUT request: {}", certificate);
        service.save(certificate);
        return OK;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntityStatus deleteCertificate(@PathVariable Integer id) {
        log.debug("Id passed in DELETE request: {}", id);
        service.deleteById(id);
        return OK;
    }
}
