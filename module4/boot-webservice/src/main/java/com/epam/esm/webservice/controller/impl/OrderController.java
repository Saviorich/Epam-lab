package com.epam.esm.webservice.controller.impl;

import com.epam.esm.webservice.controller.PageableResourceController;
import com.epam.esm.webservice.dto.OrderDTO;
import com.epam.esm.webservice.service.OrderService;
import com.epam.esm.webservice.util.Pagination;
import com.epam.esm.webservice.util.ResponseEntityStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.epam.esm.webservice.util.PaginationLinkBuilder.buildPaginationLinks;

@Log4j2
@RestController
@RequestMapping("/orders")
public class OrderController implements PageableResourceController<OrderDTO> {

    private static final ResponseEntityStatus OK = new ResponseEntityStatus(HttpStatus.OK);

    private final OrderService service;

    @Autowired
    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CollectionModel<OrderDTO>> findAll(@RequestParam(required = false, defaultValue = "1") Integer page,
                                                             @RequestParam(required = false, defaultValue = "10") Integer limit) {
        log.debug("GET request is processed in findAll, pagination info passed in request: page={}, limit={}", page, limit);

        Pagination pagination = new Pagination(page, limit);
        List<OrderDTO> orders = service.findAllByParameters(pagination);
        return ResponseEntity.ok(CollectionModel.of(orders, buildPaginationLinks(service, getClass(), pagination)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<OrderDTO> getById(@PathVariable Integer id) {
        log.debug("GET request is processed in getById method, id passed in request: {}", id);
        return ResponseEntity.ok(service.findById(id));
    }
}
