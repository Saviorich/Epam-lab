package com.epam.esm.webservice.service.exception;

public class TagNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Tag with id=%d not found";

    public TagNotFoundException(int id) {
        super(String.format(MESSAGE, id));
    }
}
