package com.epam.esm.webservice.service.impl;

import com.epam.esm.webservice.controller.CertificateController;
import com.epam.esm.webservice.dto.CertificateDTO;
import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.repository.CertificateRepository;
import com.epam.esm.webservice.service.CertificateService;
import com.epam.esm.webservice.service.exception.CertificateNotFoundException;
import com.epam.esm.webservice.service.exception.InvalidPageNumberException;
import com.epam.esm.webservice.service.exception.ResourceNotFoundException;
import com.epam.esm.webservice.util.CertificateParameters;
import com.epam.esm.webservice.util.Pagination;
import com.epam.esm.webservice.validator.PaginationValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.epam.esm.webservice.util.Resource.CERTIFICATE;
import static java.util.Collections.emptyList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
public class CertificateServiceImpl implements CertificateService {

    private static final Logger logger = LogManager.getLogger();

    private final CertificateRepository repository;
    private final PaginationValidator validator;
    private final ModelMapper mapper;

    @Autowired
    public CertificateServiceImpl(CertificateRepository repository, ModelMapper mapper, PaginationValidator validator) {
        this.repository = repository;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public List<CertificateDTO> findAllWithParams(CertificateParameters parameters) {
        int count = repository.countFilteredEntries(parameters);
        if (count == 0) {
            return emptyList();
        }
        else if (!validator.isPaginationValid(parameters.getPagination(), count)) {
            throw new InvalidPageNumberException("Invalid page number: " + parameters.getPagination().getPage());
        }
        return repository.findAllByParameters(parameters)
                .stream()
                .map(this::convertToDto)
                .map(this::addSelfRelLink)
                .collect(Collectors.toList());
    }

    @Override
    public List<CertificateDTO> findAllByParameters(Pagination pagination) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    @Override
    public CertificateDTO findById(Integer id) {
        Certificate certificate = repository.findById(id)
                .orElseThrow(() -> new CertificateNotFoundException(id));
        return addSelfRelLink(convertToDto(certificate));
    }

    @Override
    public void save(CertificateDTO certificateToSave) {
        Certificate certificate = mapper.map(certificateToSave, Certificate.class);
        if (certificateToSave.getId() != null) {
            logger.warn("Updating certificate={}", certificateToSave);
            repository.update(certificate);
        } else {
            logger.warn("Saving certificate={}", certificateToSave);
            repository.add(certificate);
        }
    }

    @Override
    public void deleteById(Integer id) {
        logger.warn("Deleting certificate with id={}", id);
        repository.deleteById(id);
        logger.warn("Certificate deleted");
    }

    @Override
    public Integer countAll() {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    @Override
    public Integer countFilteredCertificates(CertificateParameters certificateParameters) {
        return repository.countFilteredEntries(certificateParameters);
    }

    private CertificateDTO addSelfRelLink(CertificateDTO certificateDTO) {
        return certificateDTO.add(linkTo(CertificateController.class)
                .slash(certificateDTO.getId())
                .withSelfRel());
    }

    public CertificateDTO convertToDto(Certificate certificate) {
        logger.warn("Converting certificate to DTO");
        return mapper.map(certificate, CertificateDTO.class);
    }
}