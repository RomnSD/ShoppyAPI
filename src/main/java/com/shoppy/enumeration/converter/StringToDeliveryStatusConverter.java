package com.shoppy.enumeration.converter;

import com.fasterxml.jackson.databind.util.StdConverter;

import com.shoppy.enumeration.DeliveryStatus;
import com.shoppy.exception.APIException;

import org.springframework.http.HttpStatus;

public class StringToDeliveryStatusConverter extends StdConverter<String, DeliveryStatus> {

    @Override
    public DeliveryStatus convert(String source) {
        try {
            return DeliveryStatus.valueOf(StringToCountryConverter.getEnumName(source));
        }
        catch (IllegalArgumentException exception) {
            throw new APIException("Unknown delivery status", HttpStatus.NOT_FOUND);
        }
    }

}
