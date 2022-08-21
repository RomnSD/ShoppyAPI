package com.shoppy.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@NoArgsConstructor
public class ProductDTO {

    @NotNull(message = "name must not be null")
    @NotBlank(message = "name must not be in blank")
    private String name;

    @NotNull(message = "description must not be null")
    @NotBlank(message = "description must not be empty")
    private String description;

    @NotNull(message = "price must not be null")
    @PositiveOrZero(message = "price must be greater or equal to 0")
    private Double price;

    @NotNull(message = "stock must not be null")
    @PositiveOrZero(message = "stock must be greater or equal to 0")
    private Integer stock;

}
