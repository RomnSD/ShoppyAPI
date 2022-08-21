package com.shoppy.enumeration.converter;

import com.fasterxml.jackson.databind.util.StdConverter;

import com.shoppy.exception.APIException;
import com.shoppy.enumeration.City;
import org.springframework.http.HttpStatus;

public class StringToCityConverter extends StdConverter<String, City> {

    @Override
    public City convert(String source) {
        try {
            return City.valueOf(StringToCountryConverter.getEnumName(source));
        }
        catch (IllegalArgumentException exception) {
            throw new APIException("City " + source + " was not found", HttpStatus.NOT_FOUND);
        }
    }

}
