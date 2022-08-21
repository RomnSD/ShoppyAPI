package com.shoppy.broman.shoppy.service;

import com.shoppy.broman.shoppy.TestUtils;
import com.shoppy.exception.APIException;
import com.shoppy.model.Product;
import com.shoppy.repository.ProductRepository;

import com.shoppy.service.ProductService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

class ProductServiceTest {

    private final ProductRepository productRepository = Mockito.mock(ProductRepository.class);
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void whenStringAndPageableAreSet_thenResultAListOfProducts() {
        String query = "test";
        Pageable pageable = Pageable.unpaged();
        List<Product> products = List.of();

        Mockito.when(productRepository.findProductsByNameContainingIgnoreCase(query, pageable)).thenReturn(products);

        MatcherAssert.assertThat(productService.getProducts(query, null, pageable), Matchers.is(Matchers.equalTo(products)));
    }

    @Test
    void whenQueryIsPresentButEmpty_thenException() {
        String query = "";
        Sort sort = Sort.unsorted();
        Assertions.assertThrows(APIException.class, () -> productService.getProducts(query, sort, null), "Query value is empty");
    }

    @Test
    void whenQueryIsNotPresent_thenReturnAllProducts() {
        Sort sort = Sort.unsorted();
        Pageable pageable = Pageable.unpaged();
        List<Product> products = List.of();

        Mockito.when(productRepository.findAll(pageable)).thenReturn(Page.empty());

        MatcherAssert.assertThat(productService.getProducts(null, sort, pageable), Matchers.is(Matchers.equalTo(products)));
    }

    @Test
    void whenAddProductIsCalled_thenSuccess() {
        productService.addProduct(TestUtils.createProduct());
        Mockito.verify(productRepository).save(Mockito.any());
    }

    @Test
    void whenGetProductByIdIsCalled_thenSuccess() {
        Product product = TestUtils.createProduct();
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        MatcherAssert.assertThat(productService.getProductById(product.getId()), Matchers.is(Matchers.sameInstance(product)));
    }

    @Test
    void whenGetProductNotNullIsCalled_thenSuccess() {
        Product product = TestUtils.createProduct();
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        MatcherAssert.assertThat(productService.getProductNotNull(product.getId()), Matchers.is(Matchers.sameInstance(product)));
    }

    @Test
    void whenGetProductNotNullIsCalledButProductIsNull_thenException() {
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(APIException.class, () -> productService.getProductNotNull(0L), "product not found");
    }

    @Test
    void whenUpdatingAExistingProduct_thenSuccess() {
        Product product = TestUtils.createProduct();

        Mockito.when(productRepository.existsById(product.getId())).thenReturn(true);
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.updateProduct(product.getId(), product);

        Mockito.verify(productRepository).existsById(product.getId());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void whenUpdatingANotExistingProduct_thenException() {
        Product product = TestUtils.createProduct();
        Long productId = product.getId();
        Mockito.when(productRepository.existsById(productId)).thenReturn(false);
        Assertions.assertThrows(APIException.class, () -> productService.updateProduct(productId, product), "product not found");
    }

    @Test
    void whenDeletingAnExistingProduct_thenSuccess() {
        Long productId = 0L;

        Mockito.when(productRepository.existsById(productId)).thenReturn(true);

        productService.deleteProduct(productId);

        Mockito.verify(productRepository).deleteById(productId);
    }

    @Test
    void whenDeletingANotExistingProduct_thenException() {
        Long productId = 0L;

        Mockito.when(productRepository.existsById(productId)).thenReturn(false);

        Assertions.assertThrows(APIException.class, () -> productService.deleteProduct(productId), "product not found");
    }

}