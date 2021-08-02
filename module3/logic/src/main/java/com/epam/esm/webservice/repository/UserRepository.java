package com.epam.esm.webservice.repository;

import com.epam.esm.webservice.entity.User;
import com.epam.esm.webservice.util.CertificateParameters;
import com.epam.esm.webservice.util.Pagination;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAllByParameters(Pagination pagination);

    Optional<User> findById(Integer id);

    Integer countTotalEntries();
}
