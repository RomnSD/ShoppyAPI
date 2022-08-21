package com.shoppy.broman.shoppy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import com.shoppy.enumeration.City;
import com.shoppy.enumeration.Country;
import com.shoppy.enumeration.State;
import com.shoppy.model.Address;
import com.shoppy.model.CardPaymentMethod;
import com.shoppy.model.Checkout;
import com.shoppy.model.Item;
import com.shoppy.model.Product;

import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Random;
import java.util.UUID;

public final class TestUtils {

    private static final JsonMapper jsonMapper = new JsonMapper();

    public static CardPaymentMethod createPaymentMethod() {
        return createPaymentMethod(false);
    }

    public static CardPaymentMethod createPaymentMethod(boolean random) {
        CardPaymentMethod paymentMethod = new CardPaymentMethod();
        paymentMethod.setId(UUID.randomUUID());
        paymentMethod.setCardHolder("Test");
        paymentMethod.setCardNumber("0000 0000 0000 " + (random ? new Random().nextInt(1000, 9000) : "0000"));
        paymentMethod.setSecurityCode("0000");
        paymentMethod.setExpirationDate("00/00");
        return paymentMethod;
    }

    public static Address createAddress() {
        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setCountry(Country.DOMINICAN_REPUBLIC);
        address.setCity(City.LA_ROMANA);
        address.setState(State.LA_ROMANA);
        address.setZipCode(State.LA_ROMANA.getZipCodes().get(0));
        address.setAddressLine1("TEST");
        address.setAddressLine2("TEST");
        return address;
    }

    public static Item createItem() {
        Item item = new Item();
        item.setId(UUID.randomUUID());
        item.setProduct(createProduct());
        item.setQuantity(1);
        return item;
    }

    public static Item createItem(Product product) {
        Item item = new Item();
        item.setId(UUID.randomUUID());
        item.setProduct(product);
        item.setQuantity(1);
        return item;
    }

    public static Product createProduct() {
        Product product = new Product();
        product.setId(0L);
        product.setName("TEST");
        product.setDescription("TEST");
        product.setPrice(1.99D);
        product.setStock(100);
        return product;
    }

    public static Checkout createCheckout() {
        Checkout checkout = new Checkout();
        checkout.setAddress(createAddress());
        checkout.addItem(createItem(createProduct()));
        checkout.setPaymentMethod(createPaymentMethod());
        return checkout;
    }

    public static Principal getPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String toJson(Object object) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(object);
    }

}
