package com.shoppy.model;

import com.shoppy.exception.APIException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.Type;

import org.springframework.http.HttpStatus;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Type(type = "uuid-char")
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column
    private String email;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private List<CardPaymentMethod> paymentMethods = new ArrayList<>();

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    private Checkout currentCheckout;

    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }

    public void addAddress(Address address) {
        checkForDuplicatedAddresses(address);
        addresses.add(address);
    }

    public Address findAddress(UUID id) {
        return addresses.stream()
                .filter(address -> address.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new APIException("Address not found", HttpStatus.NOT_FOUND));
    }

    public void updateAddress(UUID id, Address address) {
        checkForDuplicatedAddresses(address);
        Address currentAddress = findAddress(id);
        currentAddress.setCountry(address.getCountry());
        currentAddress.setCity(address.getCity());
        currentAddress.setState(address.getState());
        currentAddress.setZipCode(address.getZipCode());
        currentAddress.setAddressLine1(address.getAddressLine1());
        currentAddress.setAddressLine2(address.getAddressLine2());
    }

    public void removeAddress(UUID id) {
        addresses.remove(findAddress(id));
    }

    public void checkForDuplicatedAddresses(Address address) {
        if (addresses.contains(address)) {
            throw new APIException("Address already existing", HttpStatus.CONFLICT);
        }
    }

    public void addOrder(Order order) {
        if (order.getId() == null) {
            orders.add(order);
        }
    }

    public void updateOrder(UUID id, Order order) {
        Order currentOrder = findOrder(id);
        currentOrder.setSummary(order.getSummary());
        currentOrder.setDeliveryStatus(order.getDeliveryStatus());
    }

    public Order findOrder(UUID id) {
        return orders.stream()
                .filter(order -> order.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new APIException("Order not found", HttpStatus.NOT_FOUND));
    }

    public void removeOrder(UUID id) {
        Order order = findOrder(id);
        orders.remove(order);
    }

    public void addPaymentMethod(CardPaymentMethod paymentMethod) {
        checkForDuplicatedPaymentMethods(paymentMethod);
        paymentMethods.add(paymentMethod);
    }

    public void updatePaymentMethod(UUID id, CardPaymentMethod paymentMethod) {
        CardPaymentMethod currentPaymentMethod = findPaymentMethod(id);
        if (currentPaymentMethod.getCardNumber().compareTo(paymentMethod.getCardNumber()) != 0) {
            checkForDuplicatedPaymentMethods(paymentMethod);
        }
        currentPaymentMethod.setCardNumber(paymentMethod.getCardNumber());
        currentPaymentMethod.setCardHolder(paymentMethod.getCardHolder());
        currentPaymentMethod.setExpirationDate(paymentMethod.getExpirationDate());
        currentPaymentMethod.setSecurityCode(paymentMethod.getSecurityCode());
    }

    public CardPaymentMethod findPaymentMethod(UUID id) {
        return paymentMethods.stream()
                .filter(paymentMethod -> paymentMethod.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new APIException("Payment method not found", HttpStatus.NOT_FOUND));
    }

    public void removePaymentMethod(UUID id) {
        if (!paymentMethods.removeIf(paymentMethod -> id.equals(paymentMethod.getId()))) {
            throw new APIException("Payment method not exists", HttpStatus.NOT_FOUND);
        }
    }

    private void checkForDuplicatedPaymentMethods(CardPaymentMethod paymentMethod) {
        if (paymentMethods.stream().anyMatch(method -> method.getCardNumber().equals(paymentMethod.getCardNumber()))) {
            throw new APIException("Payment method already existing", HttpStatus.CONFLICT);
        }
    }

}
