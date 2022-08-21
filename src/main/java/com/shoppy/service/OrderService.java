package com.shoppy.service;

import com.shoppy.exception.APIException;
import com.shoppy.model.Customer;
import com.shoppy.model.Order;
import com.shoppy.repository.OrderRepository;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    private final CustomerService customerService;
    private final OrderRepository orderRepository;

    public List<Order> getOrders(Principal principal) {
        return customerService.getCustomerByUsernameOrCreate(principal).getOrders();
    }

    public List<Order> getOrders(String username) {
        return customerService.getCustomerByUsername(username).getOrders();
    }

    public void updateOrder(UUID id, Order order) {
        Customer customer = customerService.getCostumerById(getCustomerId(id));
        customer.updateOrder(id, order);
        customerService.saveOrUpdate(customer);
    }

    public void removeOrder(UUID id) {
        Customer customer = customerService.getCostumerById(getCustomerId(id));
        customer.removeOrder(id);
        customerService.saveOrUpdate(customer);
    }

    private UUID getCustomerId(UUID orderId) {
        UUID customerId = orderRepository.getCustomerId(orderId);
        if (customerId == null) {
            throw new APIException("Order not found", HttpStatus.NOT_FOUND);
        }
        return customerId;
    }

}
