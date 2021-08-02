package com.epam.esm.webservice.service;

import com.epam.esm.webservice.dto.CertificateDTO;
import com.epam.esm.webservice.util.CertificateParameters;

import java.util.List;

public interface CertificateService extends PageableResourceService<CertificateDTO> {

    List<CertificateDTO> findAllWithParams(CertificateParameters parameters);

    CertificateDTO findById(Integer id);

    void save(CertificateDTO certificate);

    void deleteById(Integer id);

    Integer countFilteredCertificates(CertificateParameters parameters);
}
