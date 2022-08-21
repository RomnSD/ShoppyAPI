package com.shoppy.controller;

import com.shoppy.controller.dto.CardPaymentMethodDTO;
import com.shoppy.controller.mapper.EntityMapper;
import com.shoppy.service.CardPaymentMethodService;
import com.shoppy.utils.ControllerUtils;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/billing")
@AllArgsConstructor
public class BillingController {

    private final CardPaymentMethodService paymentService;
    private final EntityMapper entityMapper;

    @GetMapping
    public ResponseEntity<Object> getPaymentMethods(Principal principal) {
        return ResponseEntity.ok(paymentService.getPaymentMethods(principal));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPaymentMethod(@RequestBody @Validated CardPaymentMethodDTO dto, BindingResult errors, Principal principal) {
        ControllerUtils.checkForErrors(errors);
        paymentService.createPaymentMethod(principal, entityMapper.dtoToCardPaymentMethod(dto));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePaymentMethod(@PathVariable("id") UUID id, @RequestBody @Validated CardPaymentMethodDTO dto, BindingResult errors, Principal principal) {
        ControllerUtils.checkForErrors(errors);
        paymentService.updatePaymentMethod(principal, id, entityMapper.dtoToCardPaymentMethod(dto));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePaymentMethod(@PathVariable("id") UUID id, Principal principal) {
        paymentService.deletePaymentMethod(principal, id);
    }

}
