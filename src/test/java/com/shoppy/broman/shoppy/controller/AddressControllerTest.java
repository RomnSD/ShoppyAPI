package com.shoppy.broman.shoppy.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;

import com.shoppy.broman.shoppy.TestUtils;
import com.shoppy.controller.AddressController;
import com.shoppy.controller.mapper.EntityMapper;
import com.shoppy.enumeration.State;
import com.shoppy.exception.handler.APIExceptionHandler;
import com.shoppy.model.Address;
import com.shoppy.model.Customer;
import com.shoppy.service.AddressService;
import com.shoppy.service.CustomerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

class AddressControllerTest {

    private final CustomerService customerService = Mockito.mock(CustomerService.class);
    private final EntityMapper entityMapper = Mappers.getMapper(EntityMapper.class);
    private final Customer customer = Mockito.mock(Customer.class);
    private MockMvc mockMvc;

    private static final String URL = "/api/v1/addresses/";
    private static final String URL_LOCATIONS = "/api/v1/addresses/locations";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new AddressController(new AddressService(customerService), entityMapper)).setControllerAdvice(new APIExceptionHandler()
        ).build();
    }

    @Test
    @DisplayName("Should send all available addresses")
    void getAllStoredAddresses_thenSuccess() throws Exception {
        Principal principal = TestUtils.getPrincipal();
        List<Address> addresses = List.of(TestUtils.createAddress());

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getAddresses()).thenReturn(addresses);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").isNotEmpty()
                );
    }

    @Test
    @DisplayName("Should send all available locations")
    void whenGettingLocations_thenSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_LOCATIONS)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").isNotEmpty()
                );
    }

    @Test
    @DisplayName("Should add an unique address")
    void whenCreatingANotExistingAddress_thenSuccess() throws Exception {
        Principal principal = TestUtils.getPrincipal();
        String content = new JsonMapper().writeValueAsString(TestUtils.createAddress());

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
    }

    @Test
    @DisplayName("Should fail if trying to add a duplicated address")
    void whenCreatingAnExistingAddress_thenFail() throws Exception {
        Principal principal = TestUtils.getPrincipal();
        Address address = TestUtils.createAddress();
        String content = new JsonMapper().writeValueAsString(address);
        Customer customer = Mockito.spy(Customer.class);
        customer.addAddress(address);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isConflict(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Address already existing")
                );
    }

    @Test
    @DisplayName("Should successfully update an address if not duplicated")
    void whenUpdatingExistingAddress_thenSuccess() throws Exception {
        Principal principal = TestUtils.getPrincipal();
        Address address = TestUtils.createAddress();
        String content = new JsonMapper().writeValueAsString(address);
        Customer customer = Mockito.spy(Customer.class);

        address.setState(State.VILLA_HERMOSA);
        customer.addAddress(address);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + address.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNoContent(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
    }

    @Test
    @DisplayName("Should fail if updating a non existing address")
    void whenUpdatingExistingANotExistingAddress_thenFail() throws Exception {
        Principal principal = TestUtils.getPrincipal();
        Address address = TestUtils.createAddress();
        String content = new JsonMapper().writeValueAsString(address);
        Customer customer = Mockito.spy(Customer.class);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + address.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Address not found")
                );
    }

    @Test
    @DisplayName("Should if deleting an existing address")
    void whenDeletingAnExistingAddress_thenSuccess() throws Exception {
        Principal principal = TestUtils.getPrincipal();
        Address address = TestUtils.createAddress();
        Customer customer = Mockito.spy(Customer.class);
        customer.addAddress(address);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNoContent(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
    }

    @Test
    @DisplayName("Should fail if trying to delete a non existing address")
    void whenDeletingANotExistingAddress_thenFail() throws Exception {
        Principal principal = TestUtils.getPrincipal();
        Customer customer = Mockito.spy(Customer.class);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Address not found")
                );
    }

}