package com.ecommerce.advance.order.requestdto;

import com.ecommerce.advance.order.responsedto.OrderItemResponse;
import lombok.Data;

import java.util.List;

@Data
public class CartDto {
    private String cartId;
    private List<OrderItemResponse> items;
    private double totalAmount;
}
