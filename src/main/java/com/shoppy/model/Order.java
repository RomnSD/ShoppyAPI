package com.shoppy.model;

import com.shoppy.enumeration.DeliveryStatus;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Type(type = "uuid-char")
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @Lob
    @Column(name = "order_summary", nullable = false)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    public void setCreationDate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof Order order && Objects.equals(id, order.getId());
    }


}
