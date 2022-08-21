package com.shoppy.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class LoginCredentials {

    @NotNull(message = "username must not be null")
    @NotBlank(message = "username must not be in blank")
    private String username;

    @NotNull(message = "password must not be null")
    @NotBlank(message = "password must not be in blank")
    private String password;

}
