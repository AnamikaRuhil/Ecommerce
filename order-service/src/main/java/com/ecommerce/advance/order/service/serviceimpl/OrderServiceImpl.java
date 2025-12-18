package com.ecommerce.advance.order.service.serviceimpl;

import com.ecommerce.advance.order.kafka.OrderEventProducer;
import com.ecommerce.advance.order.model.OrderEntity;
import com.ecommerce.advance.order.repository.OrderRepository;
import com.ecommerce.advance.order.requestdto.CartDto;
import com.ecommerce.advance.order.responsedto.OrderItemResponse;
import com.ecommerce.advance.order.responsedto.OrderResponseDto;
import com.ecommerce.advance.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final String CART_URL = "http://localhost:8084/cart/";

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderEventProducer eventProducer) {
        this.orderRepository = orderRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    public OrderResponseDto createOrderFromCart(String userId) {

        CartDto cart = webClient.get()
                .uri(CART_URL + userId)
                .retrieve()
                .bodyToMono(CartDto.class)
                .block();

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart empty or not found.");
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

        eventProducer.publishOrderCreated(saved.getId(), userId, saved.getTotalAmount());

        return mapToDto(saved, "Order created successfully");
    }

    @Override
    public List<OrderResponseDto> getOrdersByUser(String userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    // ------------- MAPPING LOGIC -------------------

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