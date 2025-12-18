package com.ecommerce.advance.order.model;


import com.ecommerce.advance.order.responsedto.OrderItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "orders")
public class OrderEntity {
    @Id
    private String id;
    private String orderId;
    private String userId;
    private List<OrderItemResponse> items;
    private double totalAmount;
    private String status; // PENDING, CONFIRMED, FAILED
    private Instant createdAt;
}
