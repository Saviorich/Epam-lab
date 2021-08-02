package com.epam.esm.webservice.repository;

import com.epam.esm.webservice.entity.Order;
import com.epam.esm.webservice.util.CertificateParameters;
import com.epam.esm.webservice.util.Pagination;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    List<Order> findAllByParameters(Pagination pagination);

    List<Order> findAllByUserId(Integer userId, Pagination pagination);

    Optional<Order> findById(Integer id);

    void saveOrder(Order order);

    void deleteById(Integer id);

    Integer countTotalEntries();

    Integer countUserOrders(Integer userId);
}
