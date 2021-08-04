package com.epam.esm.webservice.service.exception;

import static com.epam.esm.webservice.util.Resource.USER;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(Integer id) {
        super(String.format("User with id=%s not found", id), USER);
    }
}
