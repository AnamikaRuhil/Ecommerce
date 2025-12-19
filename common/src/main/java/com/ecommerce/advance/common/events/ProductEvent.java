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
        private String eventType;
        private String source;
        private String message;
    }

