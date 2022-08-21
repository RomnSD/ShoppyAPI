package com.shoppy.enumeration.converter;

import com.fasterxml.jackson.databind.util.StdConverter;

import com.shoppy.exception.APIException;
import com.shoppy.enumeration.State;
import org.springframework.http.HttpStatus;

public class StringToStateConverter extends StdConverter<String, State> {

    @Override
    public State convert(String source) {
        try {
            return State.valueOf(StringToCountryConverter.getEnumName(source));
        }
        catch (IllegalArgumentException exception) {
            throw new APIException("State " + source + " was not found", HttpStatus.NOT_FOUND);
        }
    }

}
