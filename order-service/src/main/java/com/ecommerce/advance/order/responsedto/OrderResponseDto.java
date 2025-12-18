package com.ecommerce.advance.order.responsedto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private String orderId;
    private String userId;
    private List<OrderItemResponse> items;
    private double totalAmount;
    private String status;
    private Instant createdAt;
    private String message;
}
