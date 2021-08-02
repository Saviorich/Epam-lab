package com.epam.esm.webservice.controller.impl;

import com.epam.esm.webservice.controller.PageableResourceController;
import com.epam.esm.webservice.dto.OrderDTO;
import com.epam.esm.webservice.dto.UserDTO;
import com.epam.esm.webservice.service.OrderService;
import com.epam.esm.webservice.service.UserService;
import com.epam.esm.webservice.util.Pagination;
import com.epam.esm.webservice.util.ResponseEntityStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.epam.esm.webservice.util.PaginationLinkBuilder.buildPaginationLinks;

@Log4j2
@RestController
@RequestMapping("/users")
public class UserController implements PageableResourceController<UserDTO> {

    private static final ResponseEntityStatus OK = new ResponseEntityStatus(HttpStatus.OK);

    private final UserService service;
    private final OrderService orderService;

    @Autowired
    public UserController(UserService service, OrderService orderService) {
        this.service = service;
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<UserDTO>> findAll(@RequestParam(required = false, defaultValue = "1") Integer page,
                                                            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        log.debug("GET request is processed in findAll method, pagination info passed in request: page={}, limit={}", page, limit);
        Pagination pagination = new Pagination(page, limit);
        List<UserDTO> users = service.findAllByParameters(pagination);
        return ResponseEntity.ok(CollectionModel.of(users, buildPaginationLinks(service, getClass(), pagination)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        log.debug("GET request is processed in getUserById method, id passed in request: {}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<CollectionModel<OrderDTO>> findAll(@RequestParam(required = false, defaultValue = "1") Integer page,
                                                             @RequestParam(required = false, defaultValue = "10") Integer limit,
                                                             @PathVariable Integer userId) {
        log.debug("GET request is processed in findAll, pagination info passed in request: page={}, limit={}, id={}", page, limit, userId);

        Pagination pagination = new Pagination(page, limit);
        List<OrderDTO> orders = orderService.findAllByUserId(userId, pagination);
        return ResponseEntity.ok(CollectionModel.of(orders, buildPaginationLinks(userId, orderService.countUserOrders(userId), pagination)));
    }

    @GetMapping("/{userId}/orders/{orderId}")
    public OrderDTO findById(@PathVariable Integer userId,
                             @PathVariable Integer orderId) {
        log.debug("GET request is processed in findById, userId={}, orderId={}", userId, orderId);
        return orderService.findById(userId, orderId);
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntityStatus makeOrder(@RequestBody OrderDTO order, @PathVariable Integer userId) {
        log.debug("Order and id passed in POST request: {}, {}", order, userId);
        UserDTO user = new UserDTO();
        user.setId(userId);
        order.setUser(user);
        orderService.save(order);
        return OK;
    }

    @DeleteMapping("/{userId}/orders/{orderId}")
    public ResponseEntityStatus deleteOrder(@PathVariable Integer userId, @PathVariable Integer orderId) throws IllegalAccessException {
        log.debug("Deleting order with orderId={} of user with userId={}", orderId, userId);
        orderService.deleteById(userId, orderId);
        return OK;
    }
}
