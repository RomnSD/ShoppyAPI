package com.shoppy.broman.shoppy.service;

import com.shoppy.exception.APIException;
import com.shoppy.model.Customer;
import com.shoppy.repository.CustomerRepository;

import com.shoppy.service.CustomerService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
class CustomerServiceTest {

    private final CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerRepository);
    }

    @Test
    @DisplayName("Should success when and return all customers stored in the app")
    void whenGetAllCustomer_thenResultAList() {
        List<Customer> customers = List.of();
        Mockito.when(customerRepository.findAll()).thenReturn(customers);

        MatcherAssert.assertThat(customerService.getAllCustomers(), Matchers.equalTo(customers));
    }

    @Test
    @DisplayName("Should success when looking for customer and this exists")
    void whenCustomerExistsById_thenSuccess() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());

        Mockito.when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        MatcherAssert.assertThat(customerService.getCostumerById(customer.getId()), Matchers.is(customer));
    }

    @Test
    @DisplayName("Should fail when looking for a customer that doesn't exists")
    void whenCustomerNotExistsById_thenException() {
        UUID id  = UUID.randomUUID();
        Mockito.when(customerRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(APIException.class, () -> customerService.getCostumerById(id), "Customer not found");
    }

    @Test
    @DisplayName("Should success when looking for a customer by username")
    void whenCustomerExistsByUsername_thenSuccess() {
        Customer customer = new Customer();
        customer.setUsername("test");

        Mockito.when(customerRepository.findCustomerByUsername(customer.getUsername())).thenReturn(Optional.of(customer));

        MatcherAssert.assertThat(customerService.getCustomerByUsername(customer.getUsername()), Matchers.is(customer));
    }

    @Test
    @DisplayName("Should fail when looking for a customer and that doesn't exists")
    void whenCustomerNotExistsByUsername_thenException() {
        String username  = "test";
        Mockito.when(customerRepository.findCustomerByUsername(username)).thenReturn(Optional.empty());
        Assertions.assertThrows(APIException.class, () -> customerService.getCustomerByUsername(username), "Customer not found");
    }

    @Test
    @DisplayName("Should success and create a new customer if the one is not present")
    void whenGetCustomerByUsernameOrCreateIsCalledAndCustomerNotExists_thenShouldCreateNewCustomer() {
        Customer customer = new Customer();
        customer.setUsername("test");

        Mockito.when(customerRepository.findCustomerByUsername(customer.getUsername())).thenReturn(Optional.of(customer));

        MatcherAssert.assertThat(customerService.getCustomerByUsername(customer.getUsername()), Matchers.is(customer));
    }

    @Test
    @DisplayName("Should success and save a customer")
    void whenSaveOrUpdateIsCalled_shouldSave() {
        Customer customer = new Customer();

        Mockito.when(customerRepository.save(customer)).thenReturn(customer);

        MatcherAssert.assertThat(customerService.saveOrUpdate(customer), Matchers.is(customer));
    }

    @Test
    @DisplayName("Should success when deleting a customer")
    void whenCustomerExistsAndDeleteIsCalled_thenShouldDeleteIt() {
        Customer customer = new Customer();
        customer.setUsername("test");

        Mockito.when(customerRepository.findCustomerByUsername(customer.getUsername())).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(customer.getUsername());

        Mockito.verify(customerRepository).findCustomerByUsername(customer.getUsername());
        Mockito.verify(customerRepository).delete(customer);
    }

    @Test
    @DisplayName("Should fail when trying to delete a non existing customer")
    void whenCustomerNotExistsAndDeleteIsCalled_thenException() {
        String username = "test";

        Mockito.when(customerRepository.findCustomerByUsername(username)).thenReturn(Optional.empty());

        Assertions.assertThrows(APIException.class, () -> customerService.deleteCustomer(username), "Customer not found");
    }

}