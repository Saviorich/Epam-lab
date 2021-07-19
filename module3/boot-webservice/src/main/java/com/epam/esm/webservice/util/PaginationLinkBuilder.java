package com.epam.esm.webservice.util;

import com.epam.esm.webservice.controller.CertificateController;
import com.epam.esm.webservice.controller.PageableResourceController;
import com.epam.esm.webservice.controller.impl.UserController;
import com.epam.esm.webservice.service.PageableResourceService;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class PaginationLinkBuilder {

    private static final String NEXT_PAGE_RELATION_NAME = "next";
    private static final String PREV_PAGE_RELATION_NAME = "prev";
    private static final String FIRST_PAGE_RELATION_NAME = "first";
    private static final String LAST_PAGE_RELATION_NAME = "last";

    public static <T> List<Link> buildPaginationLinks(PageableResourceService<T> resourceService,
                                                      Class<? extends PageableResourceController<T>> controller,
                                                      Pagination pagination) {
        List<Link> links = new ArrayList<>();

        int limit = pagination.getLimit();
        int firstPage = 1;
        int currentPage = pagination.getPage();
        int lastPage = (int) Math.ceil((double) resourceService.countAll() / limit);

        if (currentPage > firstPage && currentPage < lastPage) {
            links.add(linkTo(methodOn(controller).findAll(currentPage + 1, limit)).withRel(NEXT_PAGE_RELATION_NAME));
            links.add(linkTo(methodOn(controller).findAll(currentPage - 1, limit)).withRel(PREV_PAGE_RELATION_NAME));
        } else if (currentPage == lastPage && currentPage > firstPage) {
            links.add(linkTo(methodOn(controller).findAll(currentPage - 1, limit)).withRel(PREV_PAGE_RELATION_NAME));
        } else if (currentPage == firstPage && currentPage < lastPage) {
            links.add(linkTo(methodOn(controller).findAll(currentPage + 1, limit)).withRel(NEXT_PAGE_RELATION_NAME));
        }

        links.add(linkTo(methodOn(controller).findAll(firstPage, limit)).withRel(FIRST_PAGE_RELATION_NAME));
        links.add(linkTo(methodOn(controller).findAll(lastPage, limit)).withRel(LAST_PAGE_RELATION_NAME));

        return links;
    }

    public static List<Link> buildPaginationLinks(Integer certificateAmount, CertificateParameters parameters, Pagination pagination) {
        List<Link> links = new ArrayList<>();
        if (certificateAmount > 0) {
            int limit = pagination.getLimit();
            int firstPage = 1;
            int currentPage = pagination.getPage();
            int lastPage = (int) Math.ceil((double) certificateAmount / limit);

            List<String> tags = parameters.getTags() == null ? Collections.emptyList() : parameters.getTags();

            Link next = linkTo(methodOn(CertificateController.class)
                    .getAllWithParams(tags, parameters.getSort(), parameters.getType(), parameters.getSearch(),
                            currentPage + 1, limit))
                    .withRel(NEXT_PAGE_RELATION_NAME);

            Link prev = linkTo(methodOn(CertificateController.class)
                    .getAllWithParams(tags, parameters.getSort(), parameters.getType(), parameters.getSearch(),
                            currentPage - 1, limit))
                    .withRel(PREV_PAGE_RELATION_NAME);

            if (currentPage > firstPage && currentPage < lastPage) {
                links.add(next);
                links.add(prev);
            } else if (currentPage == firstPage && currentPage < lastPage) {
                links.add(next);
            } else if (currentPage == lastPage && currentPage > firstPage) {
                links.add(prev);
            }

            links.add(linkTo(methodOn(CertificateController.class)
                    .getAllWithParams(tags, parameters.getSort(), parameters.getType(), parameters.getSearch(), firstPage, limit))
                    .withRel(FIRST_PAGE_RELATION_NAME));
            links.add(linkTo(methodOn(CertificateController.class)
                    .getAllWithParams(tags, parameters.getSort(), parameters.getType(), parameters.getSearch(), lastPage, limit))
                    .withRel(LAST_PAGE_RELATION_NAME));
        }
        return links;
    }

    public static List<Link> buildPaginationLinks(Integer userId, Integer totalAmount, Pagination pagination) {

        List<Link> links = new ArrayList<>();
        if (totalAmount > 0) {
            int limit = pagination.getLimit();
            int firstPage = 1;
            int currentPage = pagination.getPage();
            int lastPage = (int) Math.ceil((double) totalAmount / limit);

            if (currentPage > firstPage && currentPage < lastPage) {
                links.add(linkTo(methodOn(UserController.class).findAll(currentPage + 1, limit, userId)).withRel(NEXT_PAGE_RELATION_NAME));
                links.add(linkTo(methodOn(UserController.class).findAll(currentPage - 1, limit, userId)).withRel(PREV_PAGE_RELATION_NAME));
            } else if (currentPage == lastPage && currentPage > firstPage) {
                links.add(linkTo(methodOn(UserController.class).findAll(currentPage - 1, limit, userId)).withRel(PREV_PAGE_RELATION_NAME));
            } else if (currentPage == firstPage && currentPage < lastPage) {
                links.add(linkTo(methodOn(UserController.class).findAll(currentPage + 1, limit, userId)).withRel(NEXT_PAGE_RELATION_NAME));
            }

            links.add(linkTo(methodOn(UserController.class).findAll(firstPage, limit, userId)).withRel(FIRST_PAGE_RELATION_NAME));
            links.add(linkTo(methodOn(UserController.class).findAll(lastPage, limit, userId)).withRel(LAST_PAGE_RELATION_NAME));
        }
        return links;
    }
}
