package com.shoppy.enumeration.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import com.shoppy.model.Address;

import java.io.IOException;

public class AddressToJsonObjectConverter extends JsonSerializer<Address> {

    @Override
    public void serialize(Address address, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeStartObject();
        generator.writeObjectField("id", address.getId());
        generator.writeObjectField("country", address.getCountry() == null ? "null" : address.getCountry().getName());
        generator.writeObjectField("city", address.getCity() == null ? "null" : address.getCity().getName());
        generator.writeObjectField("state", address.getState() == null ? "null" : address.getState().getName());
        generator.writeObjectField("zipCode", address.getZipCode());
        generator.writeObjectField("addressLine1", address.getAddressLine1());
        generator.writeObjectField("addressLine2", address.getAddressLine2());
        generator.writeEndObject();
    }

}
