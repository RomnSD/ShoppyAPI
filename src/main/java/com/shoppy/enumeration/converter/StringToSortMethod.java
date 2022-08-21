package com.shoppy.enumeration.converter;

import com.shoppy.enumeration.SortMethod;
import com.shoppy.exception.APIException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class StringToSortMethod implements Converter<String, SortMethod> {

    @Override
    public SortMethod convert(String method) {
        try {
            return SortMethod.valueOf(method.toUpperCase());
        }
        catch (IllegalArgumentException exception) {
            throw new APIException("Unknown sorting method: " + method, HttpStatus.NOT_FOUND);
        }
    }

}