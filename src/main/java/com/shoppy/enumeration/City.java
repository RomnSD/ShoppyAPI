package com.shoppy.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.shoppy.enumeration.converter.StringToCityConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(converter = StringToCityConverter.class)
public enum City {
    // Dominican Republic
    LA_ROMANA("La Romana", List.of(
            State.LA_ROMANA,
            State.VILLA_HERMOSA
    )),
    SAN_PEDRO_DE_MACORIS("San Pedro de Macoris", List.of(
            State.CONSUELO,
            State.SAN_PEDRO_DE_MACORIS
    )),

    //Lithuania
    MARIAMPOLE("Mariampole", List.of(
            State.MARIAMPOLE
    ));

    private final String name;
    private final List<State> states;

    public boolean hasState(State state) {
        return states.contains(state);
    }
}