package com.epam.esm.webservice.service.exception;

public class CertificateNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Certificate with id=%d not found";

    public CertificateNotFoundException(int id) {

        super(String.format(MESSAGE, id));
    }
}