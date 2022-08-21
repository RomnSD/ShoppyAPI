package com.shoppy.service;

import com.shoppy.exception.APIException;
import com.shoppy.model.Customer;
import com.shoppy.repository.CustomerRepository;

import lombok.AllArgsConstructor;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collection;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Collection<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCostumerById(UUID id) {
        return customerRepository.findById(id).orElseThrow(() -> new APIException("Customer not found", HttpStatus.NOT_FOUND));
    }

    public Customer getCustomerByUsername(String username) {
        return customerRepository.findCustomerByUsername(username).orElseThrow(() -> new APIException("Customer not found", HttpStatus.NOT_FOUND));
    }

    public Customer getCustomerByUsernameOrCreate(Principal principal) {
        AccessToken token = ((KeycloakAuthenticationToken) principal).getAccount().getKeycloakSecurityContext().getToken();
        return customerRepository.findCustomerByUsername(token.getPreferredUsername()).orElseGet(() -> saveOrUpdate(createCustomer(token)));
    }

    public Customer saveOrUpdate(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteCustomer(String username) {
        customerRepository.delete(getCustomerByUsername(username));
    }

    private Customer createCustomer(AccessToken accessToken) {
        return Customer.builder()
                .username(accessToken.getPreferredUsername())
                .email(accessToken.getEmail())
                .name(accessToken.getGivenName())
                .surname(accessToken.getFamilyName())
                .build();
    }

}
