package com.ecommerce.advance.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent {

    private String orderId;
    private String userId;
    private Double amount;
    private String eventType;
    private String reason;
}
