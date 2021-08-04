package com.epam.esm.webservice.util;

import lombok.Getter;

@Getter
public class TokenResponse {

    private final String token;

    public TokenResponse(String token) {
        this.token = token;
    }
}
