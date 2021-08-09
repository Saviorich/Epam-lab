package com.epam.esm.webservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationController {
    @GetMapping
    public HttpStatus endpoint() {
        return HttpStatus.OK;
    }
}
