package com.shoppy.broman.shoppy.service;

import com.shoppy.broman.shoppy.TestUtils;
import com.shoppy.exception.APIException;
import com.shoppy.model.CardPaymentMethod;
import com.shoppy.model.Customer;

import com.shoppy.service.CardPaymentMethodService;
import com.shoppy.service.CustomerService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class CardPaymentMethodServiceTest {

    private final Customer customer = Mockito.mock(Customer.class);
    private final CustomerService customerService = Mockito.mock(CustomerService.class);
    private CardPaymentMethodService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new CardPaymentMethodService(customerService);
    }

    @Test
    @DisplayName("Should success when getting payment methods from principal")
    void whenGettingPaymentsMethodsFromPrincipal_thenSuccess() {
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(new Customer());

        List<CardPaymentMethod> result = paymentService.getPaymentMethods(principal);

        Mockito.verify(customerService).getCustomerByUsernameOrCreate(principal);

        MatcherAssert.assertThat(result, Matchers.is(Matchers.hasSize(0)));
    }

    @Test
    @DisplayName("Should success when getting an specific payment method")
    void whenGettingSpecificPaymentMethodAndIsFound_returnSuccess() {
        Principal principal = Mockito.mock(Principal.class);
        CardPaymentMethod paymentMethod = new CardPaymentMethod();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.findPaymentMethod(Mockito.any())).thenReturn(paymentMethod);

        CardPaymentMethod result = paymentService.getPaymentMethod(principal, Mockito.any());

        Mockito.verify(customerService).getCustomerByUsernameOrCreate(principal);
        Mockito.verify(customer).findPaymentMethod(Mockito.any());

        MatcherAssert.assertThat(result, Matchers.is(paymentMethod));
    }

    @Test
    @DisplayName("Should fail when trying to get a non existing payment method")
    void whenGettingSpecificPaymentMethodAndIsNotFound_returnException() {
        UUID id = UUID.randomUUID();
        Principal principal = Mockito.mock(Principal.class);
        Customer customer = Mockito.spy(Customer.class);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        Assertions.assertThrows(APIException.class, () -> paymentService.getPaymentMethod(principal, id));
        Mockito.verify(customerService).getCustomerByUsernameOrCreate(principal);
    }

    @Test
    @DisplayName("Should success when when creating a new payment method")
    void whenCreatingPaymentMethodAndEverythingIsFine_thenSuccess() {
        Principal principal = Mockito.mock(Principal.class);
        CardPaymentMethod paymentMethod = new CardPaymentMethod();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        paymentService.createPaymentMethod(principal, paymentMethod);

        Mockito.verify(customerService).getCustomerByUsernameOrCreate(principal);
        Mockito.verify(customer).addPaymentMethod(paymentMethod);
        Mockito.verify(customerService).saveOrUpdate(customer);
    }

    @Test
    @DisplayName("Should fail when trying to add a duplicated payment method")
    void whenCreatingPaymentMethodThatAlreadyExists_thenException() {
        Principal principal = Mockito.mock(Principal.class);
        CardPaymentMethod paymentMethod = TestUtils.createPaymentMethod();
        List<CardPaymentMethod> paymentMethods = List.of(paymentMethod);

        Customer customer = Mockito.spy(Customer.class);
        customer.setPaymentMethods(paymentMethods);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Assertions.assertThrows(APIException.class, () -> paymentService.createPaymentMethod(principal, paymentMethod));
    }

    @Test
    @DisplayName("Should success when updating an existing payment method")
    void whenUpdatingPaymentMethodAndExists_thenSuccess() {
        Principal principal = Mockito.mock(Principal.class);
        CardPaymentMethod paymentMethod = TestUtils.createPaymentMethod();

        Customer customer = Mockito.spy(Customer.class);
        customer.setPaymentMethods(List.of(paymentMethod));

        CardPaymentMethod updatedPaymentMethod = TestUtils.createPaymentMethod();
        updatedPaymentMethod.setId(paymentMethod.getId());
        updatedPaymentMethod.setSecurityCode("1111");

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        paymentService.updatePaymentMethod(principal, updatedPaymentMethod.getId(), updatedPaymentMethod);

        Mockito.verify(customerService).getCustomerByUsernameOrCreate(principal);
        Mockito.verify(customer).updatePaymentMethod(updatedPaymentMethod.getId(), updatedPaymentMethod);
        Mockito.verify(customerService).saveOrUpdate(customer);
    }

    @Test
    @DisplayName("Should fail when trying to update a non existing payment method")
    void whenUpdatingPaymentMethodThatAlreadyExist_thenException() {
        Principal principal = Mockito.mock(Principal.class);

        List<CardPaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(TestUtils.createPaymentMethod(true));
        paymentMethods.add(TestUtils.createPaymentMethod(true));

        UUID paymentId = paymentMethods.get(0).getId();
        CardPaymentMethod updatedPaymentMethod = paymentMethods.get(1);

        Customer customer = Mockito.spy(Customer.class);
        customer.setPaymentMethods(paymentMethods);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        Assertions.assertThrows(APIException.class, () -> paymentService.updatePaymentMethod(principal, paymentId, updatedPaymentMethod));
        Mockito.verify(customerService).getCustomerByUsernameOrCreate(principal);
    }

    @Test
    @DisplayName("Should success when deleting an existing payment method")
    void whenDeletingPaymentMethodThatExists_thenSuccess() {
        Principal principal = Mockito.mock(Principal.class);
        CardPaymentMethod paymentMethod = TestUtils.createPaymentMethod();

        Customer customer = Mockito.spy(Customer.class);
        customer.setPaymentMethods(new ArrayList<>(List.of(paymentMethod)));

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        paymentService.deletePaymentMethod(principal, paymentMethod.getId());

        Mockito.verify(customerService).getCustomerByUsernameOrCreate(principal);
        Mockito.verify(customer).removePaymentMethod(paymentMethod.getId());
        Mockito.verify(customerService).saveOrUpdate(customer);
    }

    @Test
    @DisplayName("Should fail when trying to delete a non existing payment method")
    void whenDeletingPaymentMethodThatNotExists_thenException() {
        UUID paymentId = UUID.randomUUID();
        Principal principal = Mockito.mock(Principal.class);
        Customer customer = Mockito.spy(Customer.class);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);

        Assertions.assertThrows(APIException.class, () -> paymentService.deletePaymentMethod(principal, paymentId));
        Mockito.verify(customerService).getCustomerByUsernameOrCreate(principal);
        Mockito.verify(customer).removePaymentMethod(paymentId);
    }

}