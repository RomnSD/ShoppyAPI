package com.shoppy.broman.shoppy.repository;

import com.shoppy.broman.shoppy.TestUtils;
import com.shoppy.model.Product;

import com.shoppy.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 10; i++) {
            Product product = TestUtils.createProduct();
            product.setId(null);
            product.setName("Product #" + i);
            product.setDescription("Description #" + i);
            productRepository.save(product);
        }
    }

    @Test
    @DisplayName("Should success when finding products matches by name")
    void whenFindProductsByNameContainingIgnoreCase1_thenSuccess() {
        List<Product> products = productRepository.findProductsByNameContainingIgnoreCase("1", Pageable.ofSize(10));
        Assertions.assertEquals(1, products.size());
        Assertions.assertEquals("Product #1", products.get(0).getName());
    }

    @Test
    @DisplayName("Should success when finding products matches by name")
    void whenFindProductsByNameContainingIgnoreCaseProduct_thenSuccess() {
        List<Product> products = productRepository.findProductsByNameContainingIgnoreCase("Product", Pageable.ofSize(10));
        Assertions.assertEquals(10, products.size());
        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals("Product #" + i, products.get(i).getName());
        }
    }

}