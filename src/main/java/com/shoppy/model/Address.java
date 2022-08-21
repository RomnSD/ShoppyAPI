package com.shoppy.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.shoppy.enumeration.City;
import com.shoppy.enumeration.Country;
import com.shoppy.enumeration.State;
import com.shoppy.enumeration.converter.AddressToJsonObjectConverter;
import com.shoppy.utils.QuickCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.ToString;
import org.hibernate.annotations.Type;
import org.springframework.http.HttpStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "addresses")
@JsonSerialize(using = AddressToJsonObjectConverter.class)
public class Address {

    @Id
    @Type(type = "uuid-char")
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Country country;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private City city;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(name = "line_1", nullable = false)
    private String addressLine1;

    @Column(name = "line_2", nullable = false)
    private String addressLine2;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        QuickCode.assertTrue(country.hasCity(city), "City doesn't belong to the provided Country", HttpStatus.NOT_FOUND);
        QuickCode.assertTrue(city.hasState(state), "State doesn't belong to the provided City", HttpStatus.NOT_FOUND);
        QuickCode.assertTrue(state.hasZipCode(zipCode), "ZipCode doesn't belong to the provided State", HttpStatus.NOT_FOUND);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof Address address) {
            return country.equals(address.getCountry())                     &&
                   city.equals(address.getCity())                           &&
                   state.equals(address.getState())                         &&
                   zipCode.equalsIgnoreCase(address.getZipCode())           &&
                   addressLine1.equalsIgnoreCase(address.getAddressLine1()) &&
                   addressLine2.equalsIgnoreCase(address.getAddressLine2());
        }
        return false;
    }

}
