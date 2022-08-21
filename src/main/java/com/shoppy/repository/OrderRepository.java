package com.shoppy.repository;

import com.shoppy.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT customers.id FROM Customer customers join customers.orders orders WHERE orders.id = ?1")
    UUID getCustomerId(UUID orderId);

}
