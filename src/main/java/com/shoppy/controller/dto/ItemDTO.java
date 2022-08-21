package com.shoppy.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ItemDTO {

    @Min(value = 1, message = "quantity must be greater or equal to 1")
    @NotNull(message = "quantity must not be null")
    private Integer quantity;

}
