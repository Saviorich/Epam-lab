package com.epam.esm.webservice.service;

import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.service.exception.CertificateNotFoundException;
import com.epam.esm.webservice.util.SortBy;
import com.epam.esm.webservice.util.SortType;

import java.util.List;

public interface CertificateService {

    void save(Certificate certificate);

    void delete(int certificateId);

    List<Certificate> getAllCertificatesByParams(List<String> tags, SortBy sortBy, SortType sortType, String toSearch);

    Certificate getById(int certificateId) throws CertificateNotFoundException;
}
