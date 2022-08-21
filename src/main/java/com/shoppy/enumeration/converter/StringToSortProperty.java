package com.shoppy.enumeration.converter;

import com.shoppy.enumeration.SortProperty;
import com.shoppy.exception.APIException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;

public class StringToSortProperty implements Converter<String, SortProperty> {

    @Override
    public SortProperty convert(String property) {
        try {
            return SortProperty.valueOf(property.toUpperCase());
        }
        catch (IllegalArgumentException exception) {
            throw new APIException("Unknown sorting property: " + property, HttpStatus.NOT_FOUND);
        }
    }

}