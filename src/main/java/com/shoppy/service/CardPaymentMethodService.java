package com.shoppy.service;

import com.shoppy.model.CardPaymentMethod;
import com.shoppy.model.Customer;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CardPaymentMethodService {

    private final CustomerService customerService;

    public List<CardPaymentMethod> getPaymentMethods(Principal principal) {
        return customerService.getCustomerByUsernameOrCreate(principal).getPaymentMethods();
    }

    public CardPaymentMethod getPaymentMethod(Principal principal, UUID id) {
        return customerService.getCustomerByUsernameOrCreate(principal).findPaymentMethod(id);
    }

    public void createPaymentMethod(Principal principal, CardPaymentMethod paymentMethod) {
        Customer customer = customerService.getCustomerByUsernameOrCreate(principal);
        customer.addPaymentMethod(paymentMethod);
        customerService.saveOrUpdate(customer);
    }

    public void updatePaymentMethod(Principal principal, UUID id, CardPaymentMethod paymentMethod) {
        Customer customer = customerService.getCustomerByUsernameOrCreate(principal);
        customer.updatePaymentMethod(id, paymentMethod);
        customerService.saveOrUpdate(customer);
    }

    public void deletePaymentMethod(Principal principal, UUID id) {
        Customer customer = customerService.getCustomerByUsernameOrCreate(principal);
        customer.removePaymentMethod(id);
        customerService.saveOrUpdate(customer);
    }

}
