package com.epam.esm.webservice.repository;

import com.epam.esm.webservice.entity.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

    Optional<User> findByEmail(String email);

}
