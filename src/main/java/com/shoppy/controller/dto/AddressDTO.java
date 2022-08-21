package com.shoppy.controller.dto;

import com.shoppy.enumeration.City;
import com.shoppy.enumeration.Country;
import com.shoppy.enumeration.State;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class AddressDTO {

    @NotNull(message = "country must not be null")
    private Country country;

    @NotNull(message = "city must not be null")
    private City city;

    @NotNull(message = "state must not be null")
    private State state;

    @NotBlank(message = "zipCode must not be null")
    private String zipCode;

    @NotBlank(message = "addressLine1 must not be null")
    private String addressLine1;

    @NotBlank(message = "addressLine2 must not be null")
    private String addressLine2;

}
