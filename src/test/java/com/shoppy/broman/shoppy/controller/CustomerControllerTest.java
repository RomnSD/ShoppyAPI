package com.shoppy.broman.shoppy.controller;

import com.shoppy.controller.CustomerController;
import com.shoppy.exception.handler.APIExceptionHandler;
import com.shoppy.model.Customer;
import com.shoppy.service.CustomerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

class CustomerControllerTest {

    private final CustomerService customerService = Mockito.mock(CustomerService.class);
    private MockMvc mockMvc;

    private static final String URL = "/api/v1/customers/";
    private static final String URL_ALL = "/api/v1/customers/all/";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new CustomerController(customerService)
        ).setControllerAdvice(new APIExceptionHandler()).build();
    }

    @Test
    @DisplayName("Should success when requesting for customer information")
    void whenRequestingCustomerInformation_thenSuccess() throws Exception {
        Customer customer = new Customer();
        Mockito.when(customerService.getCustomerByUsernameOrCreate(Mockito.any())).thenReturn(customer);

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
    @DisplayName("Should success when requesting an specific customer's information")
    void whenRequestingOtherCustomerInformation_thenSuccess() throws Exception {
        Customer customer = new Customer();
        Mockito.when(customerService.getCustomerByUsername(Mockito.any())).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").isNotEmpty()
                );
    }

    @Test
    @DisplayName("Should success when requesting all customers from the app")
    void whenRequestingAllCustomers_thenSuccess() throws Exception {
        List<Customer> customers = List.of(new Customer());
        Mockito.when(customerService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_ALL)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").isNotEmpty()
                );
    }

    @Test
    @DisplayName("Should success when deleting a customer and this exists")
    void whenDeletingACustomerAndIsPresent_thenSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNoContent(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
        Mockito.verify(customerService).deleteCustomer(Mockito.any());
    }

}