package com.epam.esm.webservice.service.impl;

import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.repository.CertificateRepository;
import com.epam.esm.webservice.service.CertificateService;
import com.epam.esm.webservice.service.exception.CertificateNotFoundException;
import com.epam.esm.webservice.util.SortBy;
import com.epam.esm.webservice.util.SortType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CertificateServiceImpl implements CertificateService {

    private static final Logger logger = LogManager.getLogger();

    private CertificateRepository certificateRepository;

    @Autowired
    public CertificateServiceImpl(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    @Override
    public void save(Certificate certificate) {
        int certificateId = certificate.getId();
        if (certificateId > 0) {
            logger.debug("Updating existing certificate with id={}", certificateId);
            List<Tag> actualTags = getById(certificate.getId())
                    .getTags();
            certificateRepository.deleteTagsFromCertificate(certificateId, actualTags
                    .stream()
                    .filter(tag -> !certificate.getTags().contains(tag)).collect(Collectors.toList()));
            certificateRepository.addTagsToCertificate(certificate);
            certificateRepository.update(certificate);
            logger.debug("Certificate updated");
        } else {
            logger.debug("Creating new certificate");
            certificateRepository.add(certificate);
            logger.debug("New certificate created");
        }
    }

    @Override
    public void delete(int certificateId) {
        logger.debug("Deleting certificate with id={}", certificateId);
        certificateRepository.delete(certificateId);
        logger.debug("Certificate deleted");
    }

    @Override
    public List<Certificate> getAllCertificatesByParams(List<String> tags, SortBy sortBy, SortType sortType, String toSearch) {
        return certificateRepository.getAllWithParams(tags, sortBy, sortType, toSearch);
    }

    @Override
    public Certificate getById(int certificateId) {
        return certificateRepository.getById(certificateId)
                .orElseThrow(() -> new CertificateNotFoundException(certificateId));
    }
}
