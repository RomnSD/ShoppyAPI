package com.shoppy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment_methods")
public class CardPaymentMethod {

    @Id
    @Type(type = "uuid-char")
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "card_holder", nullable = false)
    private String cardHolder;

    @Column(name = "expiration_date", nullable = false)
    private String expirationDate;

    @Column(name = "security_code", nullable = false)
    private String securityCode;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof CardPaymentMethod paymentMethod && cardNumber.equals(paymentMethod.getCardNumber());
    }

}
