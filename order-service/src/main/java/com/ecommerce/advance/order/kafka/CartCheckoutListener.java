package com.ecommerce.advance.order.kafka;

import com.ecommerce.advance.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CartCheckoutListener {

    private final OrderService orderService;

    /**
     * Expected message format sent by Cart-Service:
     * "CHECKOUT_STARTED for user: user123 amount: 998"
     */
    private final Pattern pattern =
            Pattern.compile("CHECKOUT_STARTED for user:\\s*(\\S+)\\s*amount:\\s*(\\d+(?:\\.\\d+)?)");

    @KafkaListener(topics = "cart-events", groupId = "order-group")
    public void listen(String message) {

        System.out.println("[Order-Service] Received cart-event: " + message);

        try {
            Matcher matcher = pattern.matcher(message);

            if (!matcher.find()) {
                System.err.println("[Order-Service] ❌ Message format invalid: " + message);
                return;
            }

            String userId = matcher.group(1);
            // String amountStr = matcher.group(2);  // optional

            System.out.println("[Order-Service] Processing checkout for user: " + userId);

            // Create order from cart
            orderService.createOrderFromCart(userId);

            System.out.println("[Order-Service] ✅ Order created successfully for user: " + userId);

        } catch (Exception ex) {
            System.err.println("[Order-Service] ❌ Failed to create order: " + ex.getMessage());
        }
    }
}