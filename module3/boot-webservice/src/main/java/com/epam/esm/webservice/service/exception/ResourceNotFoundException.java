package com.epam.esm.webservice.service.exception;

import com.epam.esm.webservice.util.Resource;

public class ResourceNotFoundException extends RuntimeException {

    private final Resource resource;

    public ResourceNotFoundException(String message, Resource resource) {
        super(message);
        this.resource = resource;
    }

    public int getResourceNumber() {
        return resource.getResourceNumber();
    }
}
