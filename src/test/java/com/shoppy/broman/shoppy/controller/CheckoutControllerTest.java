package com.shoppy.broman.shoppy.controller;

import com.shoppy.broman.shoppy.TestUtils;
import com.shoppy.controller.CheckoutController;
import com.shoppy.controller.mapper.EntityMapper;
import com.shoppy.exception.handler.APIExceptionHandler;
import com.shoppy.model.Address;
import com.shoppy.model.Checkout;
import com.shoppy.model.Customer;
import com.shoppy.model.Item;
import com.shoppy.model.Product;
import com.shoppy.repository.CheckoutRepository;
import com.shoppy.repository.CustomerRepository;
import com.shoppy.repository.ProductRepository;
import com.shoppy.service.AddressService;
import com.shoppy.service.CardPaymentMethodService;
import com.shoppy.service.CheckoutService;
import com.shoppy.service.CustomerService;
import com.shoppy.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import org.mockito.Mockito;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class CheckoutControllerTest {

    private final CustomerService customerService = Mockito.spy(new CustomerService(Mockito.mock(CustomerRepository.class)));
    private final CheckoutRepository checkoutRepository = Mockito.mock(CheckoutRepository.class);
    private final ProductRepository productRepository = Mockito.mock(ProductRepository.class);
    private final Customer customer = Mockito.mock(Customer.class);
    private MockMvc mockMvc;

    private static final String URL_CHECKOUT = "/api/v1/checkout/";
    private static final String URL_PRODUCTS = "/api/v1/checkout/products/";
    private static final String URL_ADDRESS = "/api/v1/checkout/address/";
    private static final String URL_BILLING = "/api/v1/checkout/billing/";
    private static final String URL_ORDER = "/api/v1/checkout/order/";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new CheckoutController(
                        new CheckoutService(checkoutRepository, customerService),
                        new CardPaymentMethodService(customerService),
                        new ProductService(productRepository),
                        new AddressService(customerService),
                        Mappers.getMapper(EntityMapper.class))
                ).setControllerAdvice(new APIExceptionHandler()
        ).build();
    }

    @Test
    @DisplayName("Should success when requesting for checkout and it exists")
    void whenRequestingForCheckoutAndItIsPresent_thenSuccess() throws Exception {
        Checkout checkout = TestUtils.createCheckout();

        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_CHECKOUT)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").isNotEmpty()
                );
    }

    @Test
    @DisplayName("Should fail when requesting for checkout and it doesn't exists")
    void whenRequestingForCheckoutAndItIsNotPresent_thenFail() throws Exception {
        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_CHECKOUT)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("checkout is not present")
                );
    }

    @Test
    @DisplayName("Should success when requesting for items in checkout")
    void whenRequestingForGetItemsAndCheckoutIsPresent_thenSuccess() throws Exception {
        Checkout checkout = TestUtils.createCheckout();

        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_PRODUCTS)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").isNotEmpty()
                );
    }

    @Test
    @DisplayName("Should fail when requesting items when checkout is not available")
    void whenRequestingForGetItemsAndCheckoutIsNotPresent_thenFail() throws Exception {
        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_PRODUCTS)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("checkout is not present")
                );
    }

    @Test
    @DisplayName("Should success when adding a new item to the checkout")
    void whenAddingItemToCheckout_thenSuccess() throws Exception {
        Checkout checkout = TestUtils.createCheckout();
        checkout.getItems().clear();

        Item item = TestUtils.createItem();

        Mockito.when(productRepository.findById(item.getProduct().getId())).thenReturn(Optional.of(item.getProduct()));
        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL_PRODUCTS + item.getProduct().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(item))
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
    }

    @Test
    @DisplayName("Should fail when trying to add duplicated items to checkout")
    void whenAddingDuplicatedItemToCheckout_thenFail() throws Exception {
        Checkout checkout = TestUtils.createCheckout();

        Item item = TestUtils.createItem();

        Mockito.when(productRepository.findById(item.getProduct().getId())).thenReturn(Optional.of(item.getProduct()));
        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL_PRODUCTS + item.getProduct().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(item))
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isConflict(),
                        MockMvcResultMatchers.jsonPath("$.error").value("product already existing")
                );
    }

    @Test
    @DisplayName("Should success when updating an existing item in checkout")
    void whenUpdatingAnExistingItem_thenSuccess() throws Exception {
        Checkout checkout = TestUtils.createCheckout();
        String content = TestUtils.toJson(checkout.getItems().get(0));

        Item item = checkout.getItems().get(0);
        item.setQuantity(2);

        Mockito.when(productRepository.findById(item.getProduct().getId())).thenReturn(Optional.of(item.getProduct()));
        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL_PRODUCTS + item.getProduct().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );

    }

    @Test
    @DisplayName("Should fail when trying to update a non existing item")
    void whenUpdatingANotExistingItem_thenFail() throws Exception {
        Mockito.when(productRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL_PRODUCTS + 0L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(TestUtils.createItem()))
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("product not found")
                );
    }

    @Test
    @DisplayName("Should success when removing existing item in checkout")
    void whenRemovingAnExistingItem_thenSuccess() throws Exception {
        Product product = TestUtils.createProduct();
        Checkout checkout = TestUtils.createCheckout();

        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL_PRODUCTS + product.getId())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNoContent(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
    }

    @Test
    @DisplayName("Should fail when removing a non existing item in checkout")
    void whenRemovingANotExistingItem_thenFail() throws Exception {
        Checkout checkout = TestUtils.createCheckout();

        Mockito.when(productRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL_PRODUCTS + 1L)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("product not found")
                );
    }

    @Test
    @DisplayName("Should success when requesting address and checkout exists")
    void whenRequestingAddressesAndCheckoutIsPresent_thenSuccess() throws Exception {
        Checkout checkout = TestUtils.createCheckout();

        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_ADDRESS)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").isNotEmpty()
                );
    }

    @Test
    @DisplayName("Should fail when requesting address and checkout is unavailable")
    void whenRequestingAddressesAndCheckoutNotIsPresent_thenFail() throws Exception {
        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_ADDRESS)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("checkout is not present")
                );
    }

    @Test
    @DisplayName("Should success when adding a valid delivery address")
    void whenAddingAnExistingDeliveryAddress_thenSuccess() throws Exception {
        Checkout checkout = TestUtils.createCheckout();
        List<Address> addresses = List.of(TestUtils.createAddress());

        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);
        Mockito.when(customer.getAddresses()).thenReturn(addresses);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL_ADDRESS + addresses.get(0).getId())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
    }

    @Test
    @DisplayName("Should fail if trying to add an invalid address")
    void whenAddingANotExistingDeliveryAddress_thenFail() throws Exception {
        Customer customer = new Customer();
        customer.setCurrentCheckout(TestUtils.createCheckout());

        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL_ADDRESS + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Address not found")
                );
    }

    @Test
    @DisplayName("Should success when deleting delivery address from checkout")
    void whenRemovingCurrentDeliveryAddress_thenSuccess() throws Exception {
        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(TestUtils.createCheckout());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL_ADDRESS)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNoContent(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
    }

    @Test
    @DisplayName("Should success when requesting checkout's payment method")
    void whenRequestingCurrentPaymentMethodAndCheckoutExists_thenSuccess() throws Exception {
        Checkout checkout = TestUtils.createCheckout();

        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_BILLING)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").isNotEmpty()
                );
    }

    @Test
    @DisplayName("Should fail if requesting current checkout's payment method but checkout is unavailable")
    void whenRequestingCurrentPaymentMethodAndCheckoutNotExists_thenFail() throws Exception {
        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL_BILLING)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("checkout is not present")
                );
    }

    @Test
    @DisplayName("Should success when adding a valid payment method")
    void whenAddingAnExistingPaymentMethod_thenSuccess() throws Exception {
        Customer customer = new Customer();
        customer.setPaymentMethods(List.of(TestUtils.createPaymentMethod()));
        customer.setCurrentCheckout(TestUtils.createCheckout());

        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL_BILLING + customer.getPaymentMethods().get(0).getId())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
    }

    @Test
    @DisplayName("Should fail when trying to add an invalid payment method")
    void whenAddingANotExistingPaymentMethod_thenFail() throws Exception {
        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(TestUtils.createCheckout());

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL_BILLING + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
    }

    @Test
    @DisplayName("Should success when removing payment method from checkout")
    void whenRemovingAnExistingPaymentMethod_thenSuccess() throws Exception {
        Checkout checkout = TestUtils.createCheckout();

        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL_BILLING)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNoContent(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
    }

    @Test
    @DisplayName("Should success when when checkout is ready to be processed as an order")
    void whenCheckoutIsReadyForOrder_thenSuccess() throws Exception {
        Checkout checkout = TestUtils.createCheckout();

        Mockito.doReturn(customer).when(customerService).getCustomerByUsernameOrCreate(Mockito.any());
        Mockito.when(customer.getCurrentCheckout()).thenReturn(checkout);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL_ORDER)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );

    }

}