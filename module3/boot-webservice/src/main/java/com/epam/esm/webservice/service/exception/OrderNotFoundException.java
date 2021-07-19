package com.epam.esm.webservice.service.exception;

import static com.epam.esm.webservice.util.Resource.ORDER;

public class OrderNotFoundException extends ResourceNotFoundException {

    public OrderNotFoundException(Integer id) {
        super(String.format("Order with id=%d not found", id), ORDER);
    }

}
