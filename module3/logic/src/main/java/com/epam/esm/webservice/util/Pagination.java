package com.epam.esm.webservice.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pagination {

    private Integer page;
    private Integer limit;

    public Pagination() {
    }

    public Pagination(Integer page, Integer limit) {
        this.page = page;
        this.limit = limit;
    }
}
