package com.epam.esm.webservice.service.impl;

import com.epam.esm.webservice.controller.impl.OrderController;
import com.epam.esm.webservice.dto.CertificateDTO;
import com.epam.esm.webservice.dto.OrderDTO;
import com.epam.esm.webservice.entity.Order;
import com.epam.esm.webservice.repository.OrderRepository;
import com.epam.esm.webservice.service.OrderService;
import com.epam.esm.webservice.service.exception.InvalidPageNumberException;
import com.epam.esm.webservice.service.exception.OrderNotFoundException;
import com.epam.esm.webservice.service.exception.ResourceNotFoundException;
import com.epam.esm.webservice.service.exception.UnauthorizedAccessException;
import com.epam.esm.webservice.util.Pagination;
import com.epam.esm.webservice.validator.PaginationValidator;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.epam.esm.webservice.util.Resource.ORDER;
import static java.util.Collections.emptyList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Log4j2
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final PaginationValidator validator;
    private final ModelMapper mapper;

    @Autowired
    public OrderServiceImpl(OrderRepository repository, ModelMapper mapper, PaginationValidator validator) {
        this.repository = repository;
        this.validator = validator;
        this.mapper = mapper;
    }

    @Override
    public List<OrderDTO> findAllByParameters(Pagination pagination) {
        if (!validator.isPaginationValid(pagination, repository.count())) {
            throw new InvalidPageNumberException("Invalid page number: " + pagination.getPage());
        }
        return repository.findAll(PageRequest.of(pagination.getPage() - 1, pagination.getLimit()))
                .stream()
                .map(this::convertToDto)
                .map(this::addSelfRelLink)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> findAllByUserId(Integer userId, Pagination pagination) {
        int count = repository.countAllByUserId(userId);
        if (count == 0) {
            return emptyList();
        }
        else if (!validator.isPaginationValid(pagination, count)) {
            throw new InvalidPageNumberException("Invalid page number: " + pagination.getPage());
        }
        return repository.findByUserId(userId, PageRequest.of(pagination.getPage() - 1, pagination.getLimit()))
                .stream()
                .map(this::convertToDto)
                .map(this::addSelfRelLink)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO findById(Integer id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return addSelfRelLink(convertToDto(order));
    }

    @Override
    public OrderDTO findById(Integer userId, Integer orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (!order.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Order does not belong to this user", ORDER);
        }
        return addSelfRelLink(convertToDto(order));
    }

    @Override
    public void save(OrderDTO order) {
        log.warn("Calculating cost");
        calculateCost(order);
        Order orderToSave = mapper.map(order, Order.class);
        repository.save(orderToSave);
    }

    @Override
    public void deleteById(Integer id) {
        log.warn("Deleting order with id={}", id);
        repository.deleteById(id);
        log.warn("Order deleted");
    }

    @Override
    public void deleteById(Integer userId, Integer orderId) {
        OrderDTO order = findById(orderId);
        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("This order does not belong to this user");
        }
        deleteById(orderId);
    }

    @Override
    public Integer countUserOrders(Integer userId) {
        return repository.countAllByUserId(userId);
    }

    @Override
    public Integer countAll() {
        return ((Long) repository.count()).intValue();
    }

    private void calculateCost(OrderDTO order) {
        BigDecimal cost = BigDecimal.ZERO;
        for (CertificateDTO certificate : order.getCertificates()) {
            cost = cost.add(certificate.getPrice());
        }
        log.warn("Calculated cost={}", cost);
        order.setCost(cost);
    }

    private OrderDTO addSelfRelLink(OrderDTO orderDTO) {
        return orderDTO.add(linkTo(OrderController.class)
                .slash(orderDTO.getId())
                .withSelfRel());
    }

    public OrderDTO convertToDto(Order order) {
        log.warn("Converting entity to DTO");
        return mapper.map(order, OrderDTO.class);
    }
}
