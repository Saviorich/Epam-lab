package com.epam.esm.webservice.repository;


import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.util.CertificateParameters;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository  {

    List<Certificate> findAllByParameters(CertificateParameters certificateParameters);

    Optional<Certificate> findById(Integer id);

    void add(Certificate certificate);

    void update(Certificate certificate);

    void deleteById(Integer id);

    Integer countFilteredEntries(CertificateParameters parameters);
}
