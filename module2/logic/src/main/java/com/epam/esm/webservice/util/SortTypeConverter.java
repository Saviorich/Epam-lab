package com.epam.esm.webservice.util;

import org.springframework.core.convert.converter.Converter;

public class SortTypeConverter implements Converter<String, SortType> {
    @Override
    public SortType convert(String source) {
        return SortType.valueOf(source.toUpperCase());
    }
}
