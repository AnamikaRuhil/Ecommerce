package com.ecommerce.advance.productdetail.kafka;

import com.ecommerce.advance.common.events.ProductEvent;
import com.ecommerce.advance.productdetail.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DetailEventListener {
    private final ProductDetailService detailService;
    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    @KafkaListener(topics = "product-events", groupId = "product-detail-group")
    public void listen(ProductEvent event) {
        try {
            switch (event.getEventType()) {
                case "PRODUCT_DELETED" -> {
                    detailService.softDeleteByProductId(event.getProductId());
                    kafkaTemplate.send("product-events", ProductEvent.builder()
                            .productId(event.getProductId())
                            .eventType("PRODUCT_DELETE_SUCCESS")
                            .source("detail-service")
                            .message("Detail soft delete success")
                            .build());
                }
                case "PRODUCT_PURGE_READY" -> detailService.hardDeleteByProductId(event.getProductId());
                case "PRODUCT_DELETE_FAILED" -> detailService.restoreByProductId(event.getProductId());
            }
        } catch (Exception ex) {
            //log.error("Detail handler failed: {}", ex.getMessage());
            kafkaTemplate.send("product-events", ProductEvent.builder()
                    .productId(event.getProductId())
                    .eventType("PRODUCT_DELETE_FAILED")
                    .source("detail-service")
                    .message("Detail delete failed")
                    .build());
        }
    }
}

