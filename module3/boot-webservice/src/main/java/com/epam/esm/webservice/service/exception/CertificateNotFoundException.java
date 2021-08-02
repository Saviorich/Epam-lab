package com.epam.esm.webservice.service.exception;

import static com.epam.esm.webservice.util.Resource.CERTIFICATE;

public class CertificateNotFoundException extends ResourceNotFoundException {

    public CertificateNotFoundException(int id) {
        super(String.format("Certificate with id=%d not found", id), CERTIFICATE);
    }

}
