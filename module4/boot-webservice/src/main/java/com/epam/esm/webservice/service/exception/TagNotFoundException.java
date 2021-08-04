package com.epam.esm.webservice.service.exception;

import static com.epam.esm.webservice.util.Resource.TAG;

public class TagNotFoundException extends ResourceNotFoundException {

    public TagNotFoundException(Integer id) {
        super(String.format("Tag with id=%d not found", id), TAG);
    }

}