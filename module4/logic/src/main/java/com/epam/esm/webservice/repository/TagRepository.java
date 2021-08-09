package com.epam.esm.webservice.repository;

import com.epam.esm.webservice.entity.Certificate;
import com.epam.esm.webservice.entity.Order;
import com.epam.esm.webservice.entity.Tag;
import com.epam.esm.webservice.util.Pagination;
import org.hibernate.Session;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends PagingAndSortingRepository<Tag, Integer> {

    @Query(nativeQuery = true,
            value = "SELECT tag.* FROM tag" +
            "            INNER JOIN gift_certificate_tag gct on tag.id = gct.tag_id " +
            "            INNER JOIN order_gift_certificate ogc on gct.gift_certificate_id = ogc.gift_certificate_id" +
            "            INNER JOIN `order` on ogc.order_id = `order`.id " +
            "WHERE user_id = (SELECT user_id FROM `order` " +
            "                 GROUP BY user_id " +
            "                 ORDER BY SUM(cost) DESC " +
            "                 LIMIT 1) " +
            "GROUP BY user_id, tag.id " +
            "ORDER BY COUNT(tag.id) DESC " +
            "LIMIT 1")
    Optional<Tag> findMostUsedTagOfUserWithHighestCostOfAllOrders();

}
