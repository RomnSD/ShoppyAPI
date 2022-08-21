package com.shoppy.controller;

import com.shoppy.controller.dto.AddressDTO;
import com.shoppy.controller.mapper.EntityMapper;
import com.shoppy.enumeration.Country;
import com.shoppy.service.AddressService;
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
@RequestMapping("api/v1/addresses")
@AllArgsConstructor
public class AddressController {

    private AddressService addressService;
    private EntityMapper entityMapper;

    @GetMapping
    public ResponseEntity<Object> getAddress(Principal principal) {
        return ResponseEntity.ok(addressService.getAddresses(principal));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createAddress(@Validated @RequestBody AddressDTO dto, BindingResult errors, Principal principal) {
        ControllerUtils.checkForErrors(errors);
        addressService.createAddress(principal, entityMapper.dtoToAddress(dto));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAddress(@PathVariable("id") UUID id, @Validated @RequestBody AddressDTO dto, BindingResult errors, Principal principal) {
        ControllerUtils.checkForErrors(errors);
        addressService.updateAddress(principal, id, entityMapper.dtoToAddress(dto));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable("id") UUID addressId, Principal principal) {
        addressService.deleteAddress(principal, addressId);
    }

    @GetMapping("locations")
    public ResponseEntity<Object> getLocationDetails() {
        return ResponseEntity.ok(Country.COUNTRIES);
    }

}
