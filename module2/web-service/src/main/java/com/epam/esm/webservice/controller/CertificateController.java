package com.epam.esm.webservice.controller;

import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.service.CertificateService;
import com.epam.esm.webservice.util.SortBy;
import com.epam.esm.webservice.util.SortType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    private static final Logger logger = LogManager.getLogger();

    private final CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping
    public List<Certificate> getAllCertificatesByTags(@RequestParam(value = "tags", required = false) List<String> tags,
                                                      @RequestParam(value = "sort", required = false, defaultValue = "NAME") SortBy sortBy,
                                                      @RequestParam(value = "type", required = false, defaultValue = "ASC") SortType sortType,
                                                      @RequestParam(value = "search", required = false, defaultValue = "") String search) {
        logger.debug("Parameters obtained from GET request:\ntags={},\nsortBy={},\nsortType={},\nsearch={}", tags, sortBy, sortType, search);
        return certificateService.getAllCertificatesByParams(tags, sortBy, sortType, search);
    }

    @GetMapping("/{id}")
    public Certificate getCertificateById(@PathVariable("id") int id) {
        logger.debug("Parameters obtained from GET request:\nid={}", id);
        return certificateService.getById(id);
    }

    @PostMapping
    public HttpStatus createCertificate(@RequestBody Certificate certificate) {
        logger.debug("Certificate passed in POST request:\ncertificate={}", certificate);
        certificateService.save(certificate);
        return HttpStatus.OK;
    }

    @PutMapping("/{id}")
    public HttpStatus updateCertificate(@RequestBody Certificate certificate, @PathVariable("id") int id) {
        logger.debug("Parameters passed in PUT request:\ncertificate={},\nid={}", certificate, id);
        certificate.setId(id);
        certificateService.save(certificate);
        return HttpStatus.OK;
    }

    @DeleteMapping("/{id}")
    public HttpStatus deleteCertificate(@PathVariable("id") int id) {
        logger.debug("Id passed in DELETE request:\nid={}", id);
        certificateService.delete(id);
        return HttpStatus.OK;
    }
}
