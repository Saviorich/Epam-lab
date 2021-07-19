package com.epam.esm.webservice.repository;

import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.util.Pagination;

import java.util.List;
import java.util.Optional;

public interface TagRepository {
    List<Tag> findAllByParams(Pagination pagination);

    Optional<Tag> findById(Integer id);

    Optional<Tag> findMostUsedTagOfUserWithHighestCostOfAllOrders();

    void add(Tag tag);

    void delete(Integer id);

    Integer countTotalEntries();
}
