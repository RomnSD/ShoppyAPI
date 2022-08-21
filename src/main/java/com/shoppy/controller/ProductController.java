package com.shoppy.controller;

import com.shoppy.controller.dto.ProductDTO;
import com.shoppy.controller.mapper.EntityMapper;
import com.shoppy.enumeration.SortMethod;
import com.shoppy.enumeration.SortProperty;
import com.shoppy.exception.APIException;
import com.shoppy.roles.Roles;
import com.shoppy.service.ProductService;
import com.shoppy.utils.ControllerUtils;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("api/v1/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final EntityMapper entityMapper;

    @GetMapping
    public ResponseEntity<Object> getProducts(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sort", required = false) String sortBy,
            @RequestParam(value = "sortBy", required = false) String sortProperty
    ) {
        Sort sort = getSort(sortBy, sortProperty);
        Pageable pageable = getPageable(pageNum, pageSize, sort);
        return ResponseEntity.ok(productService.getProducts(query, sort, pageable));
    }

    @PostMapping
    @RolesAllowed(Roles.ROLE_ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addProduct(@RequestBody @Validated ProductDTO product, BindingResult errors) {
        ControllerUtils.checkForErrors(errors);
        productService.addProduct(entityMapper.dtoToProduct(product));
    }

    @PutMapping("{id}")
    @RolesAllowed(Roles.ROLE_ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProduct(@PathVariable("id") Long id, @RequestBody @Validated ProductDTO dto, BindingResult errors) {
        ControllerUtils.checkForErrors(errors);
        productService.updateProduct(id, entityMapper.dtoToProduct(dto));
    }

    @DeleteMapping("{id}")
    @RolesAllowed(Roles.ROLE_ADMIN)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
    }

    private Sort getSort(String sort, String property) {
        if (sort == null) {
            if (property != null) {
                throw new APIException("'sort' field is missing in query", HttpStatus.NOT_FOUND);
            }
            return Sort.unsorted();
        }
        else {
            if (property == null) {
                property = SortProperty.NAME.name();
            }
            try {
                SortProperty.valueOf(property.toUpperCase());
            }
            catch (IllegalArgumentException exception) {
                throw new APIException("Unknown sorting property: " + property, HttpStatus.NOT_FOUND);
            }
            try {
                return switch (SortMethod.valueOf(sort.toUpperCase())) {
                    case ASC  -> Sort.by(Sort.Order.asc(property.toLowerCase()));
                    case DESC -> Sort.by(Sort.Order.desc(property.toLowerCase()));
                };
            }
            catch (IllegalArgumentException exception) {
                throw new APIException("Unknown sorting method: " + sort, HttpStatus.NOT_FOUND);
            }
        }
    }

    private Pageable getPageable(Integer pageNum, Integer pageSize, Sort sort) {
        if (pageNum == null) {
            if (pageSize != null) {
                throw new APIException("'pageNum' field is missing in query", HttpStatus.NOT_FOUND);
            }
            return null;
        }
        else {
            try {
                return PageRequest.of(pageNum, pageSize, sort);
            }
            catch (IllegalArgumentException exception) {
                // 1. Thrown when page's index is less than 0
                // 2. Thrown when page's size  is less than 0
                throw new APIException(exception.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

}
