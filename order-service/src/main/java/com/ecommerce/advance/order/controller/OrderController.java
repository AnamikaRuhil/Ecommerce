package com.ecommerce.advance.order.controller;


import com.ecommerce.advance.order.responsedto.OrderResponseDto;
import com.ecommerce.advance.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<OrderResponseDto>> getAll() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<OrderResponseDto>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @PostMapping("/create/{userId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OrderResponseDto> createFromCart(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(orderService.createOrderFromCart(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    OrderResponseDto.builder()
                            .userId(userId)
                            .message("Order failed: " + e.getMessage())
                            .build()
            );
        }
    }
}