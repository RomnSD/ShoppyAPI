package com.shoppy.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.shoppy.enumeration.converter.StringToStateConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(converter = StringToStateConverter.class)
public enum State {
    // Dominican Republic
    // La Romana
    LA_ROMANA("La Romana", List.of("22000")),
    VILLA_HERMOSA("Villa Hermosa", List.of("22000")),
    // San Pedro de Macoris
    SAN_PEDRO_DE_MACORIS("San Pedro de Macoris", List.of("23000")),
    CONSUELO("Consuelo", List.of("23000")),

    // Lithuania
    // Mariampole
    MARIAMPOLE("Mariampole", List.of("68001")),

    // NULL
    NULL(null, null);

    private final String name;
    private final List<String> zipCodes;

    public boolean hasZipCode(String zipCode) {
        return zipCodes.contains(zipCode);
    }
}
