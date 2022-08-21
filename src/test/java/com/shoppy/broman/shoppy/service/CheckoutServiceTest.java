package com.shoppy.broman.shoppy.service;

import com.shoppy.broman.shoppy.TestUtils;
import com.shoppy.exception.APIException;
import com.shoppy.model.Address;
import com.shoppy.model.CardPaymentMethod;
import com.shoppy.model.Checkout;
import com.shoppy.model.Customer;
import com.shoppy.model.Item;
import com.shoppy.model.Product;
import com.shoppy.repository.CheckoutRepository;

import com.shoppy.service.CheckoutService;
import com.shoppy.service.CustomerService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.security.Principal;

class CheckoutServiceTest {

    private final CheckoutRepository checkoutRepository = Mockito.mock(CheckoutRepository.class);
    private final CustomerService customerService = Mockito.mock(CustomerService.class);
    private final Customer customer = Mockito.mock(Customer.class);
    private CheckoutService checkoutService;

    @BeforeEach
    void setUp() {
        checkoutService = new CheckoutService(checkoutRepository, customerService);
    }

    @Test
    @DisplayName("Should success if checkout exists")
    void whenCheckoutIsPresent_thenSuccess() {
        Principal principal = Mockito.mock(Principal.class);
        Checkout checkout = new Checkout();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        MatcherAssert.assertThat(checkoutService.getCheckout(principal), Matchers.is(checkout));
    }

    @Test
    @DisplayName("Should fail when checkout is unavailable ")
    void whenCheckoutIsNotPresent_thenException() {
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(null);

        Assertions.assertThrows(APIException.class, () -> checkoutService.getCheckout(principal), "checkout is not present");
    }

    @Test
    @DisplayName("Should successfully create a checkout")
    void whenCheckoutIsNotPresent_shouldCreateIt() {
        Principal principal = Mockito.any();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(null);

        Checkout result = checkoutService.getCheckoutOrCreate(principal);

        Mockito.verify(customer).setCurrentCheckout(result);
        Mockito.verify(checkoutRepository).save(Mockito.any());
    }

    @Test
    @DisplayName("Should not create a checkout if other is present")
    void whenCheckoutIsPresent_shouldNotCreateItAgain() {
        Checkout checkout = new Checkout();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(Mockito.any())).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        Checkout result = checkoutService.getCheckoutOrCreate(Mockito.any());

        Mockito.verify(customer, Mockito.times(0)).setCurrentCheckout(Mockito.any());
        MatcherAssert.assertThat(result, Matchers.is(checkout));
    }

    @Test
    @DisplayName("Should success when adding a new item to the checkout")
    void whenAddingNewItemAndStockIsFine_thenSuccess() {
        Item item = TestUtils.createItem();
        Checkout checkout = Mockito.mock(Checkout.class);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(Mockito.any())).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        checkoutService.addItemToCheckout(Mockito.any(), item);

        Mockito.verify(checkout).addItem(item);
        Mockito.verify(checkoutRepository).save(checkout);
    }

    @Test
    @DisplayName("Should fail if trying to add an item containing wrong quantity item")
    void whenAddingNewItemAndStockIsNotCorrect_thenException() {
        Principal principal = Mockito.mock(Principal.class);

        Item item = TestUtils.createItem();
        item.setQuantity(item.getProduct().getStock() + 1);

        Checkout checkout = Mockito.spy(Checkout.class);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(Mockito.any())).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        Assertions.assertThrows(APIException.class, () -> checkoutService.addItemToCheckout(principal, item), "not enough products in stock");
    }

    @Test
    @DisplayName("Should success when updating an existing item in checkout")
    void whenUpdatingAnExistingItem_thenSuccess() {
        Principal principal = Mockito.mock(Principal.class);

        Item item1 = TestUtils.createItem();
        Item item2 = TestUtils.createItem(item1.getProduct());
        item2.setQuantity(2);

        Checkout checkout = Mockito.spy(Checkout.class);
        checkout.addItem(item1);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        checkoutService.updateItemInCheckout(principal, item2);

        Mockito.verify(checkout, Mockito.times(2)).findItemByProductId(Mockito.anyLong());
        Mockito.verify(checkoutRepository).save(checkout);
    }

    @Test
    @DisplayName("Should fail when trying to update an existing item but with wrong quantity")
    void whenUpdatingAnExistingItemButStockIsNotFine_thenException() {
        Principal principal = Mockito.mock(Principal.class);

        Item item1 = TestUtils.createItem();
        Item item2 = TestUtils.createItem(item1.getProduct());
        item2.setQuantity(item1.getProduct().getStock() + 1);

        Checkout checkout = Mockito.spy(Checkout.class);
        checkout.addItem(item1);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(Mockito.any())).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        Assertions.assertThrows(APIException.class, () -> checkoutService.updateItemInCheckout(principal, item2), "not enough products in stock");
    }

    @Test
    @DisplayName("Should fail when updating an item but checkout is not present")
    void whenUpdatingItemButCheckoutIsNotPresent_thenException() {
        Principal principal = Mockito.any();
        Item item = TestUtils.createItem();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(null);

        Assertions.assertThrows(APIException.class, () -> checkoutService.updateItemInCheckout(principal, item), "checkout is not present");
    }

    @Test
    @DisplayName("Should success when deleting an existing item from checkout")
    void whenRemovingExistingItem_thenSuccess() {
        Principal principal = Mockito.mock(Principal.class);
        Item item1 = TestUtils.createItem();
        Item item2 = TestUtils.createItem();

        Product product = TestUtils.createProduct();
        product.setId(1L);
        item2.setProduct(product);

        Checkout checkout = Mockito.spy(Checkout.class);
        checkout.addItem(item1);
        checkout.addItem(item2);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(Mockito.any())).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        checkoutService.removeProductFromCheckout(principal, item1.getProduct().getId());

        Mockito.verify(checkoutRepository).save(checkout);
        MatcherAssert.assertThat(checkout.getItems().size(), Matchers.equalTo(1));
    }

    @Test
    @DisplayName("Should success when deleting the last item from checkout, and this should disappear")
    void whenRemovingTheOnlyOneRemainingItem_thenDeleteCheckout() {
        Principal principal = Mockito.mock(Principal.class);
        Item item = TestUtils.createItem();

        Checkout checkout = Mockito.spy(Checkout.class);
        checkout.addItem(item);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(Mockito.any())).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        checkoutService.removeProductFromCheckout(principal, item.getProduct().getId());

        Mockito.verify(checkoutRepository).delete(checkout);
        MatcherAssert.assertThat(checkout.getItems().size(), Matchers.equalTo(0));
    }

    @Test
    @DisplayName("Should fail when removing an item but checkout is not present")
    void whenRemovingExistingItemButCheckoutIsNotPresent_thenException() {
        Principal principal = Mockito.mock(Principal.class);

        Item item = TestUtils.createItem();
        Long productId = item.getProduct().getId();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(null);

        Assertions.assertThrows(APIException.class, () -> checkoutService.removeProductFromCheckout(principal, productId), "checkout is not present");
    }

    @Test
    @DisplayName("Should fail when trying to remove a non existing item")
    void whenRemovingANotExistingItem_thenException() {
        Principal principal = Mockito.mock(Principal.class);

        Item item = TestUtils.createItem();
        Long productId = item.getProduct().getId();

        Checkout checkout = Mockito.spy(Checkout.class);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        Assertions.assertThrows(APIException.class, () -> checkoutService.removeProductFromCheckout(principal, productId), "product not found");
    }

    @Test
    @DisplayName("Should place an order if checkout is ready")
    void whenCheckoutIsReady_thenProcessOrder() {
        Principal principal = Mockito.mock(Principal.class);
        Checkout checkout = Mockito.spy(TestUtils.createCheckout());

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        checkoutService.processOrder(principal);

        Mockito.verify(customer).addOrder(Mockito.any());
        Mockito.verify(customer).setCurrentCheckout(null);
        Mockito.verify(customerService, Mockito.times(2)).saveOrUpdate(customer);
    }

    @Test
    @DisplayName("Should fail when trying to process order but address is missing")
    void whenOrderIsBeingProcessedButAddressIsMissing_thenException() {
        Principal principal = Mockito.mock(Principal.class);
        Checkout checkout = TestUtils.createCheckout();
        checkout.setAddress(null);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        Assertions.assertThrows(APIException.class, () -> checkoutService.processOrder(principal), "Address not provided");
    }

    @Test
    @DisplayName("Should fail when trying to process order but payment method is missing")
    void whenOrderIsBeingProcessedButPaymentMethodIsMissing_thenException() {
        Principal principal = Mockito.mock(Principal.class);
        Checkout checkout = TestUtils.createCheckout();
        checkout.setPaymentMethod(null);

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        Assertions.assertThrows(APIException.class, () -> checkoutService.processOrder(principal), "Payment method not provided");
    }

    @Test
    @DisplayName("Should success when setting checkout's delivery address")
    void whenAssigningDeliveryAddressAndCheckoutExists_thenSuccess() {
        Principal principal = Mockito.mock(Principal.class);
        Checkout checkout = TestUtils.createCheckout();
        checkout.setAddress(null);
        Address address = TestUtils.createAddress();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        checkoutService.assignDeliveryAddress(principal, address);

        Mockito.verify(checkoutRepository).save(checkout);
    }

    @Test
    @DisplayName("Should fail when trying to set delivery address but checkout is not present")
    void whenAssigningDeliveryAddressAndCheckoutNotExists_thenException() {
        Principal principal = Mockito.mock(Principal.class);
        Address address = TestUtils.createAddress();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(null);

        Assertions.assertThrows(APIException.class, () -> checkoutService.assignDeliveryAddress(principal, address), "checkout is not present");
    }

    @Test
    @DisplayName("Should success when setting payment method and checkout is present")
    void whenAssigningPaymentMethodAndCheckoutExists_thenSuccess() {
        Checkout checkout = TestUtils.createCheckout();
        checkout.setPaymentMethod(null);
        CardPaymentMethod paymentMethod = TestUtils.createPaymentMethod();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(Mockito.any())).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        checkoutService.assignPaymentMethod(Mockito.any(), paymentMethod);

        Mockito.verify(checkoutRepository).save(checkout);
    }

    @Test
    @DisplayName("Should fail when setting payment method but checkout is not present")
    void whenAssigningPaymentMethodAndCheckoutNotExists_thenException() {
        Principal principal = Mockito.mock(Principal.class);
        CardPaymentMethod paymentMethod = TestUtils.createPaymentMethod();

        Mockito.when(customerService.getCustomerByUsernameOrCreate(principal)).thenReturn(customer);
        Mockito.when(customer.getCurrentCheckout()).thenReturn(null);

        Assertions.assertThrows(APIException.class, () -> checkoutService.assignPaymentMethod(principal, paymentMethod), "checkout is not present");
    }

}