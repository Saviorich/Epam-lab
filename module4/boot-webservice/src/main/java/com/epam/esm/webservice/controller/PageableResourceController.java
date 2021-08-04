package com.epam.esm.webservice.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;

public interface PageableResourceController<T> {

    ResponseEntity<CollectionModel<T>> findAll(Integer page, Integer limit);

}
