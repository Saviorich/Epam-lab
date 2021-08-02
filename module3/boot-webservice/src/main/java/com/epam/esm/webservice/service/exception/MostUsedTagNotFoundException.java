package com.epam.esm.webservice.service.exception;

import static com.epam.esm.webservice.util.Resource.TAG;

public class MostUsedTagNotFoundException extends ResourceNotFoundException {

    public MostUsedTagNotFoundException() {
        super("Most widely used tag of user with highest cost of all orders not found", TAG);
    }
}
