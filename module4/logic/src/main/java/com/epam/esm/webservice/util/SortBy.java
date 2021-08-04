package com.epam.esm.webservice.util;

public enum SortBy {
    NAME("name"),
    CREATE_DATE("createDate");

    private final String tableName;

    SortBy(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return tableName;
    }
}
