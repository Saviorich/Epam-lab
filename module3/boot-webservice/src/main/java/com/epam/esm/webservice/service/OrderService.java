package com.epam.esm.webservice.service;

import com.epam.esm.webservice.dto.OrderDTO;
import com.epam.esm.webservice.util.Pagination;

import java.util.List;

public interface OrderService extends PageableResourceService<OrderDTO> {

    List<OrderDTO> findAllByUserId(Integer userId, Pagination pagination);

    OrderDTO findById(Integer userId, Integer orderId);

    void deleteById(Integer userId, Integer orderId) throws IllegalAccessException;

    Integer countUserOrders(Integer userId);
}
