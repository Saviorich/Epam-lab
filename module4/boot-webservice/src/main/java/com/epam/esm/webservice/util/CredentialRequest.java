package com.epam.esm.webservice.util;

import lombok.Getter;

@Getter
public class CredentialRequest {

    private final String email;
    private final String password;

    public CredentialRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
