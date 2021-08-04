package com.epam.esm.webservice.service;

import com.epam.esm.webservice.util.Pagination;

import java.util.List;

public interface PageableResourceService<T> {

    List<T> findAllByParameters(Pagination pagination);

    T findById(Integer id);

    void save(T resource);

    void deleteById(Integer id);

    Integer countAll();
}
