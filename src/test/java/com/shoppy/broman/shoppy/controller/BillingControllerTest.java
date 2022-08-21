package com.shoppy.broman.shoppy.controller;

import com.shoppy.broman.shoppy.TestUtils;
import com.shoppy.controller.BillingController;
import com.shoppy.controller.mapper.EntityMapper;
import com.shoppy.exception.handler.APIExceptionHandler;
import com.shoppy.model.CardPaymentMethod;
import com.shoppy.model.Customer;
import com.shoppy.service.CardPaymentMethodService;
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

class BillingControllerTest {

    private final CustomerService customerService = Mockito.mock(CustomerService.class);
    private final Customer customer = Mockito.mock(Customer.class);
    private MockMvc mockMvc;

    private static final String URL = "/api/v1/billing/";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new BillingController(new CardPaymentMethodService(customerService), Mappers.getMapper(EntityMapper.class))).setControllerAdvice(new APIExceptionHandler()
        ).build();
    }

    @Test
    @DisplayName("Should success when requesting existing payment methods")
    void whenRequestingPaymentMethods_thenSuccess() throws Exception {
        Principal principal = TestUtils.getPrincipal();
        List<CardPaymentMethod> paymentMethods = List.of(TestUtils.createPaymentMethod());

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getPaymentMethods()).thenReturn(paymentMethods);

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
    @DisplayName("Should success creating a new and unique payment method")
    void whenCreatingANotExistingPaymentMethod_thenSuccess() throws Exception {
        Principal principal = TestUtils.getPrincipal();
        String content = createPaymentMethod();

        Customer customer = new Customer();
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
    @DisplayName("Should fail when creating a payment method that already exists")
    void whenCreatingAnExistingPaymentMethod_thenFail() throws Exception {
        Principal principal = TestUtils.getPrincipal();
        String content = createPaymentMethod();

        Customer customer = new Customer();
        customer.addPaymentMethod(TestUtils.createPaymentMethod());

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isConflict(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Payment method already existing")
                );
    }

    @Test
    @DisplayName("Should success when updating an existing payment method")
    void whenUpdatingAnExistingPaymentMethod_thenSuccess() throws Exception {
        Principal principal = TestUtils.getPrincipal();
        CardPaymentMethod paymentMethod = TestUtils.createPaymentMethod();

        String content = createPaymentMethod(paymentMethod);

        Customer customer = new Customer();
        customer.addPaymentMethod(paymentMethod);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + paymentMethod.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNoContent(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
    }

    @Test
    @DisplayName("Should fail when updating a non existing payment method")
    void whenUpdatingANotExistingPaymentMethod_thenFail() throws Exception {
        Principal principal = TestUtils.getPrincipal();
        Customer customer = new Customer();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPaymentMethod())
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Payment method not found")
                );
    }

    @Test
    @DisplayName("Should success when deleting an existing payment method")
    void whenDeletingAnExistingPaymentMethod_thenSuccess() throws Exception {
        Principal principal = TestUtils.getPrincipal();
        CardPaymentMethod paymentMethod = TestUtils.createPaymentMethod();

        Customer customer = new Customer();
        customer.addPaymentMethod(paymentMethod);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL + paymentMethod.getId())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNoContent(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
    }

    @Test
    @DisplayName("Should fail when deleting a non existing payment method")
    void whenDeletingANotExistingPaymentMethod_thenFail() throws Exception {
        Principal principal = TestUtils.getPrincipal();
        Customer customer = new Customer();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Payment method not exists")
                );
    }

    private String createPaymentMethod(CardPaymentMethod paymentMethod) throws Exception {
        return TestUtils.toJson(paymentMethod);
    }

    private String createPaymentMethod() throws Exception {
        return TestUtils.toJson(TestUtils.createPaymentMethod());
    }

}