package com.shoppy.broman.shoppy.service;

import com.shoppy.exception.APIException;
import com.shoppy.model.Customer;
import com.shoppy.model.Order;
import com.shoppy.repository.OrderRepository;

import com.shoppy.service.CustomerService;
import com.shoppy.service.OrderService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

class OrderServiceTest {

    private final CustomerService customerService = Mockito.mock(CustomerService.class);
    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final Customer customer = Mockito.mock(Customer.class);
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(customerService, orderRepository);
    }

    @Test
    @DisplayName("Should success and return all orders stored")
    void whenGetOrdersIsCalledAndCustomerIsPresent_thenReturnAList() {
        List<Order> orders = List.of();
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getOrders()).thenReturn(orders);

        List<Order> result = orderService.getOrders(principal);
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo(orders)));
    }

    @Test
    @DisplayName("Should success and return all orders of an specific customer")
    void whenGetOrdersIsCalledAndCustomerByUsernameIsPresent_thenReturnAList() {
        List<Order> orders = List.of();
        String username = "test";

        Mockito.when(customerService.getCustomerByUsername(username)).thenReturn(customer);
        Mockito.when(customer.getOrders()).thenReturn(orders);

        List<Order> result = orderService.getOrders(username);
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo(orders)));
    }

    @Test
    @DisplayName("Should success and update an order")
    void whenUpdateOrderIsCalled_thenSuccess() {
        UUID id = UUID.randomUUID();
        Order order = new Order();

        Mockito.when(customerService.getCostumerById(Mockito.any())).thenReturn(customer);
        Mockito.when(orderRepository.getCustomerId(Mockito.any())).thenReturn(UUID.randomUUID());

        orderService.updateOrder(id, order);

        Mockito.verify(customer).updateOrder(id, order);
        Mockito.verify(customerService).saveOrUpdate(customer);
    }

    @Test
    @DisplayName("Should fail when trying to update an order of a non existing customer")
    void whenUpdateOrderIsCalledAndCustomerNotExists_thenException() {
        UUID id = UUID.randomUUID();
        Order order = new Order();

        Mockito.when(customerService.getCostumerById(Mockito.any())).thenCallRealMethod();

        Assertions.assertThrows(APIException.class, () -> orderService.updateOrder(id, order), "Customer not found");
    }

    @Test
    @DisplayName("Should fail when trying to update a non existing order")
    void whenUpdateOrderIsCalledAndOrderNotExists_thenException() {
        UUID id = UUID.randomUUID();
        Order order = new Order();

        Mockito.when(orderRepository.getCustomerId(id)).thenReturn(null);

        Assertions.assertThrows(APIException.class, () -> orderService.updateOrder(id, order), "Order not found");
    }

    @Test
    @DisplayName("Should success when order is removed")
    void whenRemoveOrderIsCalled_thenSuccess() {
        UUID id = UUID.randomUUID();

        Mockito.when(customerService.getCostumerById(Mockito.any())).thenReturn(customer);
        Mockito.when(orderRepository.getCustomerId(Mockito.any())).thenReturn(UUID.randomUUID());

        orderService.removeOrder(id);

        Mockito.verify(customer).removeOrder(id);
        Mockito.verify(customerService).saveOrUpdate(customer);
    }

    @Test
    @DisplayName("Should fail when trying to remove a non existing order")
    void whenRemoveOrderIsCalledAndOrderNotExists_thenException() {
        UUID id = UUID.randomUUID();

        Mockito.when(orderRepository.getCustomerId(Mockito.any())).thenReturn(null);

        Assertions.assertThrows(APIException.class, () -> orderService.removeOrder(id), "Order not found");
    }

}