package com.shoppy.controller;

import com.shoppy.roles.Roles;
import com.shoppy.service.CustomerService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;

@RestController
@RequestMapping("api/v1/customers")
@AllArgsConstructor
public class CustomerController {

    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<Object> getInformation(Principal principal) {
        return ResponseEntity.ok(customerService.getCustomerByUsernameOrCreate(principal));
    }

    @GetMapping("{username}")
    @RolesAllowed(Roles.ROLE_ADMIN)
    public ResponseEntity<Object> getCustomerInformation(@PathVariable("username") String username) {
        return ResponseEntity.ok(customerService.getCustomerByUsername(username));
    }

    @GetMapping("all")
    @RolesAllowed(Roles.ROLE_ADMIN)
    public ResponseEntity<Object> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @DeleteMapping("{username}")
    @RolesAllowed(Roles.ROLE_ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable("username") String username) {
        customerService.deleteCustomer(username);
    }

}
