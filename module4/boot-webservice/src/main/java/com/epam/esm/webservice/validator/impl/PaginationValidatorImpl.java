package com.epam.esm.webservice.validator.impl;

import com.epam.esm.webservice.util.Pagination;
import com.epam.esm.webservice.validator.PaginationValidator;
import org.springframework.stereotype.Component;

@Component
public class PaginationValidatorImpl implements PaginationValidator {

    @Override
    public boolean isPaginationValid(Pagination pagination, long totalAmount) {
        return pagination != null &&
                pagination.getLimit() > 0 &&
                pagination.getPage() > 0 &&
                pagination.getPage() <= Math.ceil(((double) totalAmount / pagination.getLimit()));
    }
}
