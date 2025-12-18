package com.ecommerce.advance.order.kafka;

import com.ecommerce.advance.common.events.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public OrderEventProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(String orderId, String userId, double amount) {
        try {
            OrderEvent event = OrderEvent.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .amount(amount)
                    .eventType("ORDER_CREATED")
                    .build();
            kafkaTemplate.send("order-events",event);
        } catch (Exception e) {
            // log
            System.err.println("Failed to publish ORDER_CREATED: " + e.getMessage());
        }
    }

    public void publishOrderFailed(String userId, String reason) {
        try {
            OrderEvent event = OrderEvent.builder()
                    .userId(userId)
                    .eventType("ORDER_FAILED")
                    .reason(reason)
                    .build();
            kafkaTemplate.send("order-events",event);

        } catch (Exception e) {
            System.err.println("Failed to publish ORDER_FAILED: " + e.getMessage());
        }
    }
}