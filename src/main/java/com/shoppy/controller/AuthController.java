package com.shoppy.controller;

import com.shoppy.controller.dto.LoginCredentials;
import com.shoppy.service.AuthService;
import com.shoppy.utils.ControllerUtils;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "login")
    public ResponseEntity<Object> login(@Validated @RequestBody LoginCredentials credentials, Errors errors, Principal principal) {
        ControllerUtils.checkForErrors(errors);
        return ResponseEntity.ok(authService.login(principal, credentials.getUsername(), credentials.getPassword()));
    }

    @GetMapping("refresh/{refresh_token}")
    public ResponseEntity<Object> refresh(@PathVariable("refresh_token") String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @GetMapping("logout/{refresh_token}")
    public ResponseEntity<Object> logout(@PathVariable("refresh_token") String refreshToken) {
        return ResponseEntity.ok(authService.logout(refreshToken));
    }

}
