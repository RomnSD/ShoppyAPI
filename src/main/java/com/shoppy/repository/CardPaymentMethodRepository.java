package com.shoppy.repository;

import com.shoppy.model.CardPaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CardPaymentMethodRepository extends JpaRepository<CardPaymentMethod, UUID> {

}
