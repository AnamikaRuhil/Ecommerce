package com.ecommerce.advance.cart.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendCheckoutEvent(String userId, double amount) {
        String message = "CHECKOUT_STARTED for user: " + userId + " amount: " + amount;
        kafkaTemplate.send("cart-events", message);
        System.out.println("EVENT SENT: " + message);
    }
}
