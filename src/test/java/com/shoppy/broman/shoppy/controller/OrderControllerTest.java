package com.shoppy.broman.shoppy.controller;

import com.shoppy.broman.shoppy.TestUtils;
import com.shoppy.controller.OrderController;
import com.shoppy.controller.mapper.EntityMapper;
import com.shoppy.enumeration.DeliveryStatus;
import com.shoppy.exception.handler.APIExceptionHandler;
import com.shoppy.model.Order;
import com.shoppy.service.OrderService;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import org.mockito.ArgumentCaptor;
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

class OrderControllerTest {

    private final OrderService orderService = Mockito.mock(OrderService.class);
    private MockMvc mockMvc;

    private static final String URL = "/api/v1/orders/";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new OrderController(orderService, Mappers.getMapper(EntityMapper.class))
        ).setControllerAdvice(new APIExceptionHandler()).build();
    }


    @Test
    @DisplayName("Should success when requesting all customer orders")
    void whenRequestingAllOrders_thenSuccess() throws Exception {
        List<Order> orders = List.of(new Order());
        Mockito.when(orderService.getOrders(Mockito.nullable(Principal.class))).thenReturn(orders);

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
    @DisplayName("Should success when requesting orders of a specific customer ")
    void whenRequestingAllOrdersOfAnyCustomer_thenSuccess() throws Exception {
        List<Order> orders = List.of(new Order());
        Mockito.when(orderService.getOrders(Mockito.nullable(String.class))).thenReturn(orders);

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
    @DisplayName("Should success when updating an existing order")
    void whenUpdatingExistingOrder_thenSuccess() throws Exception {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setSummary("TEST");
        order.setDeliveryStatus(DeliveryStatus.SHIPPED);

        ArgumentCaptor<Order> argumentCaptor = ArgumentCaptor.forClass(Order.class);
        Mockito.doNothing().when(orderService).updateOrder(Mockito.any(), argumentCaptor.capture());

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(order))
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNoContent(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
        MatcherAssert.assertThat(argumentCaptor.getValue().getSummary(), Matchers.is(Matchers.equalTo(order.getSummary())));
    }

    @Test
    @DisplayName("Should success when deleting an existing order")
    void whenRemovingExistingOrder_thenSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        ArgumentCaptor<UUID> argumentCaptor = ArgumentCaptor.forClass(UUID.class);
        Mockito.doNothing().when(orderService).removeOrder(argumentCaptor.capture());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL + id)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNoContent(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
        MatcherAssert.assertThat(argumentCaptor.getValue(), Matchers.is(Matchers.equalTo(id)));
    }

}