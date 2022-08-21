package com.shoppy.model;

import com.shoppy.exception.APIException;

import lombok.AllArgsConstructor;
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
@Entity
@Table(name = "checkouts")
public class Checkout {

    @Id
    @Type(type = "uuid-char")
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @OneToOne
    private Address address;

    @OneToOne
    private CardPaymentMethod paymentMethod;

    @JoinColumn(name = "checkout_id")
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();

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

    public void addItem(Item item) {
        if (findItemByProductId(item.getProduct().getId()) != null) {
            throw new APIException("product already existing", HttpStatus.CONFLICT);
        }
        items.add(item);
    }

    public Item findItem(UUID id) {
        return items.stream().filter(item -> item.getId().equals(id)).findFirst().orElse(null);
    }

    public boolean containsItem(UUID id) {
        return findItem(id) != null;
    }

    public void updateItem(Item item) {
        findItemByProductId(item.getProduct().getId()).setQuantity(item.getQuantity());
    }

    public Item findItemByProductId(Long id) {
        return items.stream().filter(item -> item.getProduct().getId().equals(id)).findFirst().orElse(null);
    }

    public boolean containsProduct(Long id) {
        return findItemByProductId(id) != null;
    }

    public boolean deleteItem(UUID id) {
        return items.removeIf(item -> item.getId().equals(id));
    }

    public boolean deleteItemByProductId(Long id) {
        return items.removeIf(item -> id.equals(item.getProduct().getId()));
    }

}
