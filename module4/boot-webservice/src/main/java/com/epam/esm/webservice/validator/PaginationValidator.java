package com.epam.esm.webservice.validator;

import com.epam.esm.webservice.util.Pagination;

public interface PaginationValidator {

    boolean isPaginationValid(Pagination pagination, long totalAmount);
}
