package com.epam.esm.webservice.util;

public enum Resource {

    CERTIFICATE(1),
    TAG(2),
    ORDER(3),
    USER(4);

    private final int resourceNumber;

    Resource(int resourceNumber) {
        this.resourceNumber = resourceNumber;
    }

    public int getResourceNumber() {
        return resourceNumber;
    }
}
