package com.ecommerce.advance.order.service.serviceimpl;

import com.ecommerce.advance.order.exception.BusinessException;
import com.ecommerce.advance.order.exception.DataNotFoundException;
import com.ecommerce.advance.order.kafka.OrderEventProducer;
import com.ecommerce.advance.order.model.OrderEntity;
import com.ecommerce.advance.order.repository.OrderRepository;
import com.ecommerce.advance.order.requestdto.CartDto;
import com.ecommerce.advance.order.responsedto.OrderItemResponse;
import com.ecommerce.advance.order.responsedto.OrderResponseDto;
import com.ecommerce.advance.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderEventProducer eventProducer;
    private final WebClient webClient = WebClient.create();

    @Value("${cart.service.url}")
    private final String cartUrl;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderEventProducer eventProducer, String cartUrl) {
        this.orderRepository = orderRepository;
        this.eventProducer = eventProducer;
        this.cartUrl = cartUrl;
    }

    @Override
    public OrderResponseDto createOrderFromCart(String userId) {
        log.info("Create order from cart : userId={}", userId);
        CartDto cart;
        try {
             cart = webClient.get()
                    .uri(cartUrl + "/{userId}", userId)
                    .retrieve()
                    .bodyToMono(CartDto.class)
                    .block();
        }
        catch (Exception ex) {
            log.error("Failed to fetch cart for userId={}", userId, ex);
            throw new BusinessException("Unable to fetch cart for order creation");
        }

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            log.warn("Order creation failed – cart empty for userId={}", userId);
            throw new BusinessException("Cart is empty or not found");
        }

        OrderEntity order = OrderEntity.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .items(cart.getItems().stream()
                        .map(i -> new OrderItemResponse(i.getProductId(), i.getName(), i.getQuantity(), i.getPrice()))
                        .toList())
                .totalAmount(cart.getTotalAmount())
                .status("CONFIRMED")
                .createdAt(Instant.now())
                .build();

        OrderEntity saved = orderRepository.save(order);
        log.info("Order saved | orderId={}, userId={}, amount={}",
                saved.getId(), userId, saved.getTotalAmount());

        eventProducer.publishOrderCreated(saved.getId(), userId, saved.getTotalAmount());

        log.info("OrderCreated event published | orderId={}", saved.getId());
        return mapToDto(saved, "Order created successfully");
    }

    @Override
    public List<OrderResponseDto> getOrdersByUser(String userId) {
        log.info("Fetching orders for user {}", userId);
        List<OrderEntity> orders = orderRepository.findByUserId(userId);
        if (orders.isEmpty()) {
            log.warn("No orders found for user {}", userId);
            throw new DataNotFoundException("No orders found for user");
        }
        return orders.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        log.info("Fetching all orders");
        List<OrderEntity> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            log.warn("No orders found");
            throw new DataNotFoundException("No orders available");
        }
        return orders.stream()
                .map(this::mapToDto)
                .toList();
    }


    private OrderResponseDto mapToDto(OrderEntity order) {
        return mapToDto(order, null);
    }

    private OrderResponseDto mapToDto(OrderEntity order, String message) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> new OrderItemResponse(i.getProductId(), i.getName(), i.getQuantity(), i.getPrice()))
                .toList();

        return OrderResponseDto.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .items(items)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .message(message)
                .build();
    }
}