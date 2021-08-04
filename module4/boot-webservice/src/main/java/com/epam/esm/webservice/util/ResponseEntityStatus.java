package com.epam.esm.webservice.util;

import org.springframework.http.HttpStatus;

/**
 * This class is used to return http status as json
 * @see HttpStatus
 * */
public class ResponseEntityStatus {

    private final HttpStatus status;

    public ResponseEntityStatus(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
