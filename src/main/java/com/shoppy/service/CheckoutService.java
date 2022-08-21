package com.shoppy.service;

import com.shoppy.enumeration.DeliveryStatus;
import com.shoppy.exception.APIException;
import com.shoppy.model.Address;
import com.shoppy.model.CardPaymentMethod;
import com.shoppy.model.Checkout;
import com.shoppy.model.Customer;
import com.shoppy.model.Item;
import com.shoppy.model.Order;
import com.shoppy.repository.CheckoutRepository;
import com.shoppy.utils.QuickCode;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@AllArgsConstructor
public class CheckoutService {

    private final CheckoutRepository checkoutRepository;
    private final CustomerService customerService;

    public Checkout getCheckout(Principal principal) {
        Checkout checkout = customerService.getCustomerByUsernameOrCreate(principal).getCurrentCheckout();
        return QuickCode.getNotNull(checkout, "checkout is not present", HttpStatus.NOT_FOUND);
    }

    public Checkout getCheckoutOrCreate(Principal principal) {
        Customer customer = customerService.getCustomerByUsernameOrCreate(principal);
        if (customer.getCurrentCheckout() == null) {
            customer.setCurrentCheckout(checkoutRepository.save(new Checkout()));
        }
        return customer.getCurrentCheckout();
    }

    public void addItemToCheckout(Principal principal, Item item) {
        checkStock(item);
        Checkout checkout = getCheckoutOrCreate(principal);
        checkout.addItem(item);
        checkoutRepository.save(checkout);
    }

    public void updateItemInCheckout(Principal principal, Item item) {
        checkStock(item);
        Checkout checkout = getCheckout(principal);
        checkout.updateItem(item);
        checkoutRepository.save(checkout);
    }

    public void removeProductFromCheckout(Principal principal, Long productId) {
        Checkout checkout = getCheckout(principal);
        if (checkout.deleteItemByProductId(productId)) {
            if (checkout.getItems().isEmpty()) {
                Customer customer = customerService.getCustomerByUsernameOrCreate(principal);
                customer.setCurrentCheckout(null);
                deleteCheckout(checkout);
            }
            else {
                checkoutRepository.save(checkout);
            }
        }
        else {
            throw new APIException("product not found", HttpStatus.NOT_FOUND);
        }
    }

    public void processOrder(Principal principal) {
        Customer customer = customerService.getCustomerByUsernameOrCreate(principal);
        // Add order
        customer.addOrder(createOrder(getCheckout(principal)));
        customerService.saveOrUpdate(customer);

        // Remove current order
        customer.setCurrentCheckout(null);
        customerService.saveOrUpdate(customer);
    }

    public void assignDeliveryAddress(Principal principal, Address address) {
        Checkout checkout = getCheckout(principal);
        checkout.setAddress(address);
        checkoutRepository.save(checkout);
    }

    public void assignPaymentMethod(Principal principal, CardPaymentMethod address) {
        Checkout checkout = getCheckout(principal);
        checkout.setPaymentMethod(address);
        checkoutRepository.save(checkout);
    }

    public void deleteCheckout(Checkout checkout) {
        checkoutRepository.delete(checkout);
    }

    private void checkStock(Item item) {
        if (item.getQuantity() > item.getProduct().getStock()) {
            throw new APIException("not enough products in stock", HttpStatus.CONFLICT);
        }
    }

    private Order createOrder(Checkout checkout) {
        if (checkout.getAddress() == null) {
            throw new APIException("Address not provided", HttpStatus.BAD_REQUEST);
        }
        if (checkout.getPaymentMethod() == null) {
            throw new APIException("Payment method not provided", HttpStatus.BAD_REQUEST);
        }
        StringBuilder summary = new StringBuilder();
        summary.append("Shoppy Application").append('\n');
        summary.append("Items: ").append('\n');
        checkout.getItems().forEach(item ->
            summary.append(item.getProduct().getName()).append('(').append(item.getQuantity()).append(')').append(' ').append('$').append(item.getProduct().getPrice()).append('\n')
        );
        summary.append('\n');
        summary.append("Payment method: ").append('\n');
        summary.append("Card holder: ").append(checkout.getPaymentMethod().getCardHolder()).append('\n');
        summary.append("Card starting numbers: ").append(checkout.getPaymentMethod().getCardNumber(), 0, 4).append('\n');

        summary.append('\n');
        summary.append("Delivery address: ").append('\n');
        summary.append("Country: ").append(checkout.getAddress().getCountry()).append('\n');
        summary.append("City: ").append(checkout.getAddress().getCity()).append('\n');
        summary.append("State: ").append(checkout.getAddress().getState()).append('\n');
        summary.append("ZipCode: ").append(checkout.getAddress().getZipCode()).append('\n');
        summary.append("Line 1: ").append(checkout.getAddress().getAddressLine1()).append('\n');
        summary.append("Line 2: ").append(checkout.getAddress().getAddressLine2()).append('\n');
        summary.append("Thank you for your order");

        Order order = new Order();
        order.setSummary(summary.toString());
        order.setDeliveryStatus(DeliveryStatus.ORDER_SUMMITED);

        return order;
    }

}
