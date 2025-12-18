package com.ecommerce.advance.common.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ProductEvent {
        private Long productId;
        private String eventType; // PRODUCT_DELETED, PRODUCT_DELETE_FAILED, PRODUCT_PURGE_READY
        private String source;    // e.g., product-service
        private String message;
    }

