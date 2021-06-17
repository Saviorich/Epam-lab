package com.epam.esm.webservice.repository;

import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.util.SortBy;
import com.epam.esm.webservice.util.SortType;

import java.util.List;
import java.util.Optional;


public interface CertificateRepository {

    void update(Certificate certificate);

    void add(Certificate certificate);

    void delete(int certificateId);

    Optional<Certificate> getById(int certificateId);

    List<Certificate> getAllWithParams(List<String> tags, SortBy sortBy, SortType sortType, String toSearch);

    void addTagsToCertificate(Certificate certificate);

    void deleteTagsFromCertificate(int certificateId, List<Tag> tagsToDelete);
}
