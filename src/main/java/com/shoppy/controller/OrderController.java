package com.shoppy.controller;

import com.shoppy.controller.dto.OrderDTO;
import com.shoppy.controller.mapper.EntityMapper;
import com.shoppy.roles.Roles;
import com.shoppy.service.OrderService;
import com.shoppy.utils.ControllerUtils;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final EntityMapper entityMapper;

    @GetMapping
    public ResponseEntity<Object> getOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getOrders(principal));
    }

    @GetMapping("{username}")
    public ResponseEntity<Object> getOrders(@PathVariable("username") String username) {
        return ResponseEntity.ok(orderService.getOrders(username));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RolesAllowed(Roles.ROLE_ADMIN)
    public void updateOrder(@PathVariable("id") UUID id, @Validated @RequestBody OrderDTO dto, BindingResult errors) {
        ControllerUtils.checkForErrors(errors);
        orderService.updateOrder(id, entityMapper.dtoToOrder(dto));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RolesAllowed(Roles.ROLE_ADMIN)
    public void removeOrder(@PathVariable("id") UUID id) {
        orderService.removeOrder(id);
    }

}
