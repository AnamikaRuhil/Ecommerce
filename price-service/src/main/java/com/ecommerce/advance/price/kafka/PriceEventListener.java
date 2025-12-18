package com.ecommerce.advance.price.kafka;

import com.ecommerce.advance.common.events.ProductEvent;
import com.ecommerce.advance.price.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceEventListener {
    private final PriceService priceService;
    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    @KafkaListener(topics = "product-events", groupId = "product-price-group")
    public void listen(ProductEvent event) {
        try {
            switch (event.getEventType()) {
                case "PRODUCT_DELETED" -> {
                    priceService.softDeleteByProductId(event.getProductId());
                    kafkaTemplate.send("product-events", ProductEvent.builder()
                            .productId(event.getProductId())
                            .eventType("PRODUCT_DELETE_SUCCESS")
                            .source("price-service")
                            .message("Price soft delete success")
                            .build());
                }
                case "PRODUCT_PURGE_READY" -> priceService.hardDeleteByProductId(event.getProductId());
                case "PRODUCT_DELETE_FAILED" -> priceService.restoreProductPrice(event.getProductId());
            }
        } catch (Exception ex) {
            //log.error("Price handler failed: {}", ex.getMessage());
            kafkaTemplate.send("product-events", ProductEvent.builder()
                    .productId(event.getProductId())
                    .eventType("PRODUCT_DELETE_FAILED")
                    .source("price-service")
                    .message("Price delete failed")
                    .build());
        }
    }
}
