package com.epam.esm.webservice.repository;

import com.epam.esm.webservice.entity.Order;
import com.epam.esm.webservice.util.CertificateParameters;
import com.epam.esm.webservice.util.Pagination;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Integer> {

    List<Order> findByUserId(Integer userId, Pageable pageable);

    Integer countAllByUserId(Integer userId);
}
