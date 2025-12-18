package com.ecommerce.advance.order.service;


import com.ecommerce.advance.order.responsedto.OrderResponseDto;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrderFromCart(String userId);

    List<OrderResponseDto> getOrdersByUser(String userId);

    List<OrderResponseDto> getAllOrders();
}
