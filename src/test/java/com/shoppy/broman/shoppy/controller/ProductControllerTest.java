package com.shoppy.broman.shoppy.controller;

import com.shoppy.broman.shoppy.TestUtils;
import com.shoppy.controller.ProductController;
import com.shoppy.controller.mapper.EntityMapper;
import com.shoppy.exception.handler.APIExceptionHandler;
import com.shoppy.model.Product;
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

class ProductControllerTest {

    private final ProductService productService = Mockito.mock(ProductService.class);
    private MockMvc mockMvc;

    private static final String URL = "/api/v1/products/";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new ProductController(productService, Mappers.getMapper(EntityMapper.class))
        ).setControllerAdvice(new APIExceptionHandler()).build();
    }

    @Test
    @DisplayName("Should success when requesting products with all properties set")
    void whenRequestingAllProductsAndAllPropertiesAreSet_thenSuccess() throws Exception {
        List<Product> products = List.of(TestUtils.createProduct());

        Mockito.when(productService.getProducts(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + String.format("?query=%s&pageNum=%s&pageSize=%s&sort=%s&sortBy=%s", "test", 0, 1, "asc", "price"))
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").isNotEmpty()
                );

    }

    @Test
    @DisplayName("Should success when requesting products without specifying a sorting method/property ")
    void whenRequestingAllProductsUnsorted_thenSuccess() throws Exception {
        List<Product> products = List.of(TestUtils.createProduct());

        Mockito.when(productService.getProducts(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + String.format("?query=%s&pageNum=%s&pageSize=%s", "test", 0, 1))
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").isNotEmpty()
                );

    }

    @Test
    @DisplayName("Should fail when sort method is null but sort property isn't")
    void whenSortIsNullButPropertyNoThen_thenFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + String.format("?query=%s&pageNum=%s&pageSize=%s&sortBy=%s", "test", 0, 1, "price"))
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("'sort' field is missing in query")
                );

    }

    @Test
    @DisplayName("Should use default property when sortBy is missing")
    void whenPropertyIsMissingShouldUseDefault_thenSuccess() throws Exception {
        List<Product> products = List.of(TestUtils.createProduct());
        Mockito.when(productService.getProducts(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + String.format("?query=%s&pageNum=%s&pageSize=%s&sort=%s", "test", 0, 1, "asc"))
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").isNotEmpty()
                );

    }

    @Test
    @DisplayName("Should fail when sorting method is unknown")
    void whenSortingMethodIsNotValid_thenFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + String.format("?query=%s&pageNum=%s&pageSize=%s&sort=%s", "test", 0, 1, "null"))
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Unknown sorting method: null")
                );

    }

    @Test
    @DisplayName("Should fail when sorting property is unknown")
    void whenSortingPropertyIsNotValid_thenFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + String.format("?query=%s&pageNum=%s&pageSize=%s&sort=%s&sortBy=%s", "test", 0, 1, "asc", "null"))
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Unknown sorting property: null")
                );

    }

    @Test
    @DisplayName("Should fail when pageNum is missing but pageSize is set")
    void whenPageNumPropertyIsMissing_thenFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + String.format("?query=%s&pageSize=%s&sort=%s&sortBy=%s", "test", 1, "desc", "name"))
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.jsonPath("$.error").value("'pageNum' field is missing in query")
                );

    }

    @Test
    @DisplayName("Should use default settings when pageNum and pageSize are missing")
    void whenPageNumAndPageSizeAreMissingReturnDefault_thenSuccess() throws Exception {
        List<Product> products = List.of(TestUtils.createProduct());
        Mockito.when(productService.getProducts(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + String.format("?query=%s&sort=%s", "test", "asc"))
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$").isNotEmpty()
                );
    }

    @Test
    @DisplayName("Should fail if index page is less than 0")
    void whenPageNumIsTooLow_thenFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + String.format("?query=%s&pageNum=%s&pageSize=%s&sort=%s&sortBy=%s", "test", -1, -1, "asc", "name"))
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.jsonPath("$.error").value("Page index must not be less than zero")
                );
    }

    @Test
    @DisplayName("Should success when creating a new product")
    void whenCreatingProduct_thenSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(TestUtils.createProduct()))
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNoContent(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
        Mockito.verify(productService).addProduct(Mockito.any(Product.class));
    }

    @Test
    @DisplayName("Should success when updating an existing product")
    void whenUpdatingAnExistingProduct_thenSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + 0L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(TestUtils.createProduct()))
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNoContent(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
        Mockito.verify(productService).updateProduct(Mockito.anyLong(), Mockito.any(Product.class));
    }

    @Test
    @DisplayName("Should success when deleting an existing product")
    void whenDeletingAProduct_thenSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL + 0L)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.log())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNoContent(),
                        MockMvcResultMatchers.jsonPath("$").doesNotExist()
                );
        Mockito.verify(productService).deleteProduct(Mockito.anyLong());
    }

}