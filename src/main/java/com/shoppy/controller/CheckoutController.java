package com.shoppy.controller;

import com.shoppy.controller.dto.ItemDTO;
import com.shoppy.controller.mapper.EntityMapper;
import com.shoppy.model.Item;
import com.shoppy.service.AddressService;
import com.shoppy.service.CardPaymentMethodService;
import com.shoppy.service.CheckoutService;
import com.shoppy.service.ProductService;
import com.shoppy.utils.ControllerUtils;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/checkout")
@AllArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final CardPaymentMethodService paymentService;
    private final ProductService productService;
    private final AddressService addressService;
    private final EntityMapper entityMapper;

    @GetMapping
    public ResponseEntity<Object> getCheckout(Principal principal) {
        return ResponseEntity.ok(checkoutService.getCheckout(principal));
    }

    @GetMapping("products")
    public ResponseEntity<Object> getItems(Principal principal) {
        return ResponseEntity.ok(checkoutService.getCheckout(principal).getItems());
    }

    @PostMapping("products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addItemToCheckout(@PathVariable("productId") Long productId, @Validated @RequestBody ItemDTO dto, BindingResult errors, Principal principal) {
        ControllerUtils.checkForErrors(errors);
        checkoutService.addItemToCheckout(principal, getItem(productId, dto));
    }

    @PutMapping("products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateItemInCheckout(@PathVariable("productId") Long productId, @Validated @RequestBody ItemDTO dto, BindingResult errors, Principal principal) {
        ControllerUtils.checkForErrors(errors);
        checkoutService.updateItemInCheckout(principal, getItem(productId, dto));
    }

    @DeleteMapping("products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItemFromCheckout(@PathVariable("productId") Long productId, Principal principal) {
        checkoutService.removeProductFromCheckout(principal, productId);
    }

    @GetMapping("address")
    public ResponseEntity<Object> getAddress(Principal principal) {
        return ResponseEntity.ok(checkoutService.getCheckout(principal).getAddress());
    }

    @PostMapping("address/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addDeliveryAddress(@PathVariable("id") UUID id, Principal principal) {
        checkoutService.assignDeliveryAddress(principal, addressService.getAddress(principal, id));
    }

    @DeleteMapping("address")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDeliveryAddress(Principal principal) {
        checkoutService.assignDeliveryAddress(principal, null);
    }

    @GetMapping("billing")
    public ResponseEntity<Object> getPaymentMethod(Principal principal) {
        return ResponseEntity.ok(checkoutService.getCheckout(principal).getPaymentMethod());
    }

    @PostMapping("billing/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addPaymentMethod(@PathVariable("id") UUID id, Principal principal) {
        checkoutService.assignPaymentMethod(principal, paymentService.getPaymentMethod(principal, id));
    }

    @DeleteMapping("billing")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePaymentMethod(Principal principal) {
        checkoutService.assignPaymentMethod(principal, null);
    }

    @PostMapping("order")
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrder(Principal principal) {
        checkoutService.processOrder(principal);
    }

    private Item getItem(Long productId, ItemDTO dto) {
        Item item = entityMapper.dtoToItem(dto);
        item.setProduct(productService.getProductNotNull(productId));
        return item;
    }

}
