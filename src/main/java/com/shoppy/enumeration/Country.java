package com.shoppy.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.shoppy.enumeration.converter.StringToCountryConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(converter = StringToCountryConverter.class)
public enum Country {
    DOMINICAN_REPUBLIC("Dominican Republic", List.of(
            City.LA_ROMANA,
            City.SAN_PEDRO_DE_MACORIS
    )),
    LITHUANIA("Lithuania", List.of(
            City.MARIAMPOLE
    ));

    public static final List<Country> COUNTRIES = List.of(Country.values());

    private final String name;
    private final List<City> cities;

    public boolean hasCity(City city) {
        return cities.contains(city);
    }
}