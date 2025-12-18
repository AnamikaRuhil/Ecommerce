package com.ecommerce.advance.order.requestdto;

import com.ecommerce.advance.order.responsedto.OrderItemResponse;
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
public class OrderRequestDto {
    private String userId;
    private List<OrderItemResponse> items;
    private double totalAmount;
    private String status; // PENDING, CONFIRMED, FAILED
    private Instant createdAt;
}
