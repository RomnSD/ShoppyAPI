package com.shoppy.enumeration.converter;

import com.fasterxml.jackson.databind.util.StdConverter;

import com.shoppy.exception.APIException;
import com.shoppy.enumeration.Country;
import org.springframework.http.HttpStatus;

public class StringToCountryConverter extends StdConverter<String, Country> {

    public static String getEnumName(String source) {
        return source.replace(" ", "_").toUpperCase();
    }

    @Override
    public Country convert(String source) {
        try {
            return Country.valueOf(getEnumName(source));
        }
        catch (IllegalArgumentException exception) {
            throw new APIException("Country " + source + " was not found", HttpStatus.NOT_FOUND);
        }
    }

}
