package com.shoppy.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class CardPaymentMethodDTO {

    @NotNull(message = "card number must not be null")
    @Pattern(regexp = "(^\\d{4}\\s\\d{4}\\s\\d{4}\\s\\d{4})", message = "card number malformed, expecting something similar to: 0000 00000 0000 0000")
    private String cardNumber;

    @NotNull(message = "card holder must not be null")
    private String cardHolder;

    @NotNull(message = "expiration date must not be null")
    @Pattern(regexp = "(^\\d{2}/\\d{2})", message = "expiration date malformed, expecting something similar to 01/22")
    private String expirationDate;

    @NotNull(message = "security code must not be null")
    @Pattern(regexp = "(^\\d{4})", message = "security code malformed, expecting something similar to: 0000")
    private String securityCode;

}
