package com.shoppy.broman.shoppy.service;

import com.shoppy.broman.shoppy.TestUtils;
import com.shoppy.model.Address;
import com.shoppy.model.Customer;

import com.shoppy.service.AddressService;
import com.shoppy.service.CustomerService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

class AddressServiceTest {

    private Customer customer;
    private CustomerService customerService;
    private AddressService addressService;

    @BeforeEach
    void setUp() {
        customer = Mockito.mock(Customer.class);
        customerService = Mockito.mock(CustomerService.class);
        addressService = new AddressService(customerService);
    }

    @Test
    @DisplayName("Should success when requesting all customer's stored address")
    void testGetAddressesReturnsCollection() {
        List<Address> addresses = List.of();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(Mockito.any())).thenReturn(customer);
        Mockito.when(customer.getAddresses()).thenReturn(addresses);

        Collection<Address> result = addressService.getAddresses(null);

        Mockito.verify(customer).getAddresses();
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo(addresses)));
    }

    @Test
    @DisplayName("Should success when getting an specific id")
    void getAddress() {
        Address address = new Address();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(Mockito.any())).thenReturn(customer);
        Mockito.when(customer.findAddress(Mockito.any())).thenReturn(address);

        Address result = addressService.getAddress(null, null);

        Mockito.verify(customer).findAddress(Mockito.any());
        MatcherAssert.assertThat(result, Matchers.is(address));
    }

    @Test
    @DisplayName("Should success when creating an address")
    void createAddress() {
        Address address = TestUtils.createAddress();
        Mockito.when(customerService.getCustomerByUsernameOrCreate(Mockito.any())).thenReturn(customer);

        addressService.createAddress(Mockito.any(), address);

        Mockito.verify(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.verify(customer).addAddress(address);
        Mockito.verify(customerService).saveOrUpdate(customer);
    }

    @Test
    @DisplayName("Should success when updating an address")
    void updateAddress() {
        UUID uuid = UUID.randomUUID();
        Address address = TestUtils.createAddress();
        Mockito.when(customerService.getCustomerByUsernameOrCreate(Mockito.any())).thenReturn(customer);

        addressService.updateAddress(Mockito.any(), uuid, address);

        Mockito.verify(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.verify(customer).updateAddress(uuid, address);
        Mockito.verify(customerService).saveOrUpdate(customer);
    }

    @Test
    @DisplayName("Should success when deleting an address")
    void deleteAddress() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(customerService.getCustomerByUsernameOrCreate(Mockito.any())).thenReturn(customer);

        addressService.deleteAddress(Mockito.any(), uuid);

        Mockito.verify(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.verify(customer).removeAddress(uuid);
        Mockito.verify(customerService).saveOrUpdate(customer);
    }


}