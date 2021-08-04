package com.epam.esm.webservice.repository.impl;

import com.epam.esm.webservice.config.TestConfig;
import com.epam.esm.webservice.repository.UserRepository;
import com.epam.esm.webservice.util.Pagination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestConfig.class)
@ContextConfiguration(classes = UserRepository.class)
@ActiveProfiles("test")
class UserRepositoryImplTest {

    @Autowired
    private UserRepository repository;

    @ParameterizedTest
    @CsvSource({"1,1", "1,2", "1,3", "1,4", "1,5"})
    void testFindAllByParameters_ShouldReturnLimitedAmount(Integer page, Integer limit) {
        Pagination pagination = new Pagination(page, limit);
        assertEquals((long)limit, repository.findAll(PageRequest.of(pagination.getPage() - 1, pagination.getLimit())).getTotalElements());
    }

    @Test
    void testFindById_ShouldReturnExistingValue() {
        String expectedEmail = "random@mail.ru";
        System.out.println(repository.findById(1).get());
        assertEquals(expectedEmail, repository.findById(1).get().getEmail());
    }

    @Test
    void testFindById_ShouldNotPresent() {
        assertFalse(repository.findById(152).isPresent());
    }
}