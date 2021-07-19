package com.epam.esm.webservice.validator.impl;

import com.epam.esm.webservice.util.Pagination;
import com.epam.esm.webservice.validator.PaginationValidator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PaginationValidatorImpl.class})
class PaginationValidatorImplTest {

    @Autowired
    private PaginationValidator validator;

    @ParameterizedTest
    @CsvSource({"2,3,6", "3,3,9", "1,5,10", "5,9,50", "9,4,90", "1,1,1"})
    void testIsPageValidWithTwoParameters_ShouldReturnTrue(Integer page, Integer limit, Integer totalAmount) {
        Pagination pagination = new Pagination(page, limit);
        assertTrue(validator.isPaginationValid(pagination, totalAmount));
    }

    @ParameterizedTest
    @CsvSource({"0,10", "-1,3", "11,10", "2,1", "100,1"})
    void testIsPageValidWithTwoParameters_ShouldReturnFalse(Integer page, Integer totalAmount) {
        Pagination pagination = new Pagination(page, 5);
        assertFalse(validator.isPaginationValid(pagination, totalAmount));
    }
}