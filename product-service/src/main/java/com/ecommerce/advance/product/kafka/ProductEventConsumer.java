package com.ecommerce.advance.product.kafka;

import com.ecommerce.advance.common.events.ProductEvent;
import com.ecommerce.advance.product.saga.SagaTracker;
import com.ecommerce.advance.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventConsumer {
    private final ProductService productService;
    private final SagaTracker sagaTracker;
    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    @KafkaListener(topics = "product-events", groupId = "product-group")
    public void listen(ProductEvent event) {
        // log.info("ProductService received event: {}", event);
        switch (event.getEventType()) {
            case "PRODUCT_DELETE_SUCCESS" -> {
                sagaTracker.confirm(event.getProductId());
                if (sagaTracker.isAllConfirmed(event.getProductId())) {
                    // log.info("All confirmations for {}, sending PRODUCT_PURGE_READY", event.getProductId());
                    ProductEvent purge = ProductEvent.builder()
                            .productId(event.getProductId())
                            .eventType("PRODUCT_PURGE_READY")
                            .source("product-service")
                            .message("Ready to purge")
                            .build();
                    kafkaTemplate.send("product-events", purge);
                    sagaTracker.clear(event.getProductId());
                }
            }
            case "PRODUCT_DELETE_FAILED" -> {
                // log.warn("Received PRODUCT_DELETE_FAILED for {}, restoring", event.getProductId());
                productService.restore(event.getProductId());
            }
            case "PRODUCT_PURGE_READY" -> {
                // log.info("Received PRODUCT_PURGE_READY for {}, hard deleting", event.getProductId());
                productService.hardDelete(event.getProductId());
            }
        }
    }
}
