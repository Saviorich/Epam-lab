package com.epam.esm.webservice.util;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CertificateParameters {
    private List<String> tags;
    private SortBy sort;
    private SortType type;
    private String search;
    private Pagination pagination;

    public CertificateParameters() {
    }

    public CertificateParameters(List<String> tags, SortBy sort, SortType type, String search, Pagination pagination) {
        this.tags = tags;
        this.sort = sort;
        this.type = type;
        this.search = search;
        this.pagination = pagination;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CertificateParameters{");
        sb.append("tags=").append(tags);
        sb.append(", sort=").append(sort);
        sb.append(", type=").append(type);
        sb.append(", search='").append(search).append('\'');
        sb.append(", pagination=").append(pagination);
        sb.append('}');
        return sb.toString();
    }
}
