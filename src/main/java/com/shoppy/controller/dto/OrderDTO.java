package com.shoppy.controller.dto;

import com.shoppy.enumeration.DeliveryStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class OrderDTO {

    @NotNull(message = "summary must not be null")
    @NotBlank(message = "summary must not be in blank")
    private String summary;

    @NotNull(message = "delivery status must not be null")
    private DeliveryStatus deliveryStatus;

}
