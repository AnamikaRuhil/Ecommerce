package com.ecommerce.advance.cart.service.serviceimpl;

import com.ecommerce.advance.cart.kafka.CartEventProducer;
import com.ecommerce.advance.cart.model.CartEntity;
import com.ecommerce.advance.cart.repository.CartRepository;
import com.ecommerce.advance.cart.requestdto.CartRequestDto;
import com.ecommerce.advance.cart.responsedto.CartResponseDto;
import com.ecommerce.advance.cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;

@Service
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartEventProducer eventProducer;
    private final WebClient webClient = WebClient.create();

    private final String PRODUCT_URL = "http://localhost:8083/product/get/";

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartEventProducer eventProducer) {
        this.cartRepository = cartRepository;
        this.eventProducer = eventProducer;
    }

    public CartResponseDto addToCart(String userId, CartRequestDto requestDto) {

        // Validate product exists by calling Product service
        var productResponse = webClient.get()
                .uri(PRODUCT_URL + requestDto.getProductId())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (productResponse == null)
            throw new RuntimeException("Product not found");

        CartEntity cart = cartRepository.findById(userId)
                .orElse(CartEntity.builder()
                        .cartId(userId)
                        .items(new ArrayList<>())
                        .totalAmount(0.0)
                        .build()
                );

        cart.getItems().add(requestDto);
        cart.setTotalAmount(cart.getTotalAmount() + (requestDto.getPrice() * requestDto.getQuantity()));

        cart =  cartRepository.save(cart);
        return CartResponseDto.builder()
                .cartId(cart.getCartId())
                .items(cart.getItems())
                .totalAmount(cart.getTotalAmount())
                .build();
    }

    public CartResponseDto getCart(String userId) {
        CartEntity cart =  cartRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return  CartResponseDto.builder()
                .cartId(cart.getCartId())
                .items(cart.getItems())
                .totalAmount(cart.getTotalAmount())
                .build();
    }

    public String checkout(String userId) {
        CartEntity cart =  cartRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // send event to Kafka
        eventProducer.sendCheckoutEvent(userId, cart.getTotalAmount());

        return "Checkout started for user " + userId;
    }
}
