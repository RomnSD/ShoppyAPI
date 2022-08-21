package com.shoppy.service;

import com.shoppy.exception.APIException;
import com.shoppy.model.Product;
import com.shoppy.repository.ProductRepository;

import com.shoppy.utils.QuickCode;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getProducts(String query, Sort sort, Pageable pageable) {
        if (pageable == null) {
            pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
        }
        if (query != null) {
            if (query.isBlank()) {
                throw new APIException("Query value is empty");
            }
            return productRepository.findProductsByNameContainingIgnoreCase(query, pageable);
        }
        else {
            return productRepository.findAll(pageable).getContent();
        }
    }

    public void addProduct(Product product) {
        productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product getProductNotNull(Long id) {
        return QuickCode.getNotNull(getProductById(id), "product not found", HttpStatus.NOT_FOUND);
    }

    public void updateProduct(Long id, Product product) {
        checkProductExistence(id);
        product.setId(id);
        productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        checkProductExistence(id);
        productRepository.deleteById(id);
    }

    private void checkProductExistence(Long id) {
        if (!productRepository.existsById(id)) {
            throw new APIException("product not found", HttpStatus.NOT_FOUND);
        }
    }

}
