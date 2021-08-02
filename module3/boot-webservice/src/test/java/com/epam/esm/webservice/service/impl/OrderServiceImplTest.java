package com.epam.esm.webservice.service.impl;

import com.epam.esm.webservice.config.ApplicationConfig;
import com.epam.esm.webservice.dto.OrderDTO;
import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Order;
import com.epam.esm.webservice.repository.OrderRepository;
import com.epam.esm.webservice.repository.impl.CertificateRepositoryImpl;
import com.epam.esm.webservice.repository.impl.OrderRepositoryImpl;
import com.epam.esm.webservice.service.CertificateService;
import com.epam.esm.webservice.service.OrderService;
import com.epam.esm.webservice.service.exception.InvalidPageNumberException;
import com.epam.esm.webservice.service.exception.OrderNotFoundException;
import com.epam.esm.webservice.service.exception.ResourceNotFoundException;
import com.epam.esm.webservice.util.Pagination;
import com.epam.esm.webservice.validator.PaginationValidator;
import com.epam.esm.webservice.validator.impl.PaginationValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ApplicationConfig.class)
@ContextConfiguration(classes = {OrderRepositoryImpl.class, ModelMapper.class, PaginationValidatorImpl.class, CertificateServiceImpl.class, CertificateRepositoryImpl.class})
@ActiveProfiles("prod")
class OrderServiceImplTest {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PaginationValidator validator;

    @Mock
    private OrderRepository repository;

    private OrderService service;

    @BeforeEach
    void setup() {
        service = new OrderServiceImpl(repository, mapper, validator);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    void findAllWithParams_ShouldReturnLimitedAmount(int limit) {
        Pagination pagination = new Pagination(1, limit);
        Order[] orders = new Order[limit];
        for (int i = 0; i < limit; i++) {
            orders[i] = new Order();
        }
        List<Order> expected = asList(orders);
        when(repository.findAllByParameters(pagination)).thenReturn(expected);
        assertEquals(expected.size(), service.findAllByParameters(pagination).size());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -3, -4, 0})
    void testGetAllByParameters_ShouldThrowInvalidPageNumberExceptionForEachParameter(Integer page) {
        Pagination pagination = new Pagination(page, 5);
        assertThrows(InvalidPageNumberException.class, () -> service.findAllByParameters(pagination));
    }

    @Test
    void testGetById_ShouldReturnDto() {
        int id = 1;
        Order expected = new Order();
        when(repository.findById(id)).thenReturn(Optional.of(expected));
        assertEquals(mapper.map(expected, OrderDTO.class), service.findById(id));
    }

    @Test
    void testGetById_ShouldThrowException() {
        when(repository.findById(1)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> service.findById(1));
    }

    @Test
    void testMakeOrder_ShouldCalculatePriceAndSave() {
        int id = 1;
        Order order = new Order();
        order.setCertificates(Collections.singletonList(new Certificate("", "", new BigDecimal("0"), 1)));
        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
        doNothing().when(repository).saveOrder(order);
        service.save(mapper.map(order, OrderDTO.class));
        when(repository.findById(id)).thenReturn(Optional.of(order));
        OrderDTO expected = new OrderDTO();
        assertEquals(expected, service.findById(id));
    }

    @Test
    void testDelete_ShouldReturnValueThenThrowResourceNotFoundException() {
        int id = 1;
        when(repository.findById(id)).thenReturn(Optional.of(new Order())).thenReturn(Optional.empty());
        assertNotNull(service.findById(id));
        service.deleteById(id);
        verify(repository).deleteById(id);
        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }
}