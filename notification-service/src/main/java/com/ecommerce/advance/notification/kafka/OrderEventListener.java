package com.ecommerce.advance.notification.kafka;

import com.ecommerce.advance.common.events.OrderEvent;
import com.ecommerce.advance.notification.requestdto.NotificationRequestDto;
import com.ecommerce.advance.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public OrderEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void listen(OrderEvent event) {

        String msg = switch (event.getEventType()) {
            case "ORDER_CREATED" -> "Your order has been placed successfully!";
            case "ORDER_FAILED" -> "Your order could not be processed.";
            default -> "Received event: " + event.getEventType();
        };

        NotificationRequestDto notification = NotificationRequestDto.builder()
                .userId(event.getUserId())
                .eventType(event.getEventType())
                .message(msg)
                .build();

        notificationService.saveNotification(notification);

    }
}