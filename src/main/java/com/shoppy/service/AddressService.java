package com.shoppy.service;

import com.shoppy.model.Customer;
import com.shoppy.model.Address;

import com.shoppy.utils.QuickCode;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AddressService {

    private final CustomerService customerService;

    public List<Address> getAddresses(Principal principal) {
        return customerService.getCustomerByUsernameOrCreate(principal).getAddresses();
    }

    public Address getAddress(Principal principal, UUID id) {
        return customerService.getCustomerByUsernameOrCreate(principal).findAddress(id);
    }

    public void createAddress(Principal principal, Address address) {
        validateAddress(address);
        Customer customer = customerService.getCustomerByUsernameOrCreate(principal);
        customer.addAddress(address);
        customerService.saveOrUpdate(customer);
    }

    public void updateAddress(Principal principal, UUID id, Address address) {
        validateAddress(address);
        Customer customer = customerService.getCustomerByUsernameOrCreate(principal);
        customer.updateAddress(id, address);
        customerService.saveOrUpdate(customer);
    }

    public void deleteAddress(Principal principal, UUID id) {
        Customer customer = customerService.getCustomerByUsernameOrCreate(principal);
        customer.removeAddress(id);
        customerService.saveOrUpdate(customer);
    }

    private void validateAddress(Address address) {
        QuickCode.assertTrue(address.getCountry().hasCity(address.getCity()), "City is not part of the provided Country", HttpStatus.NOT_FOUND);
        QuickCode.assertTrue(address.getCity().hasState(address.getState()), "State is not part of the provided City", HttpStatus.NOT_FOUND);
        QuickCode.assertTrue(address.getState().hasZipCode(address.getZipCode()), "Zip-code is not part of the provided State", HttpStatus.NOT_FOUND);
    }

}
