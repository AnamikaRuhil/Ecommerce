package com.ecommerce.advance.cart.service.serviceimpl;

import com.ecommerce.advance.cart.exception.BusinessException;
import com.ecommerce.advance.cart.exception.DataNotFoundException;
import com.ecommerce.advance.cart.kafka.CartEventProducer;
import com.ecommerce.advance.cart.model.CartEntity;
import com.ecommerce.advance.cart.repository.CartRepository;
import com.ecommerce.advance.cart.requestdto.CartRequestDto;
import com.ecommerce.advance.cart.responsedto.CartResponseDto;
import com.ecommerce.advance.cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;

@Service
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartEventProducer eventProducer;
    private final WebClient webClient = WebClient.create();

    @Value("${product.service.url}")
    private String productUrl;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartEventProducer eventProducer) {
        this.cartRepository = cartRepository;
        this.eventProducer = eventProducer;
    }

    public CartResponseDto addToCart(String userId, CartRequestDto requestDto) {

        log.info("Add to cart :: userId={}, productId={}, qty={}",
                userId, requestDto.getProductId(), requestDto.getQuantity());
        String authHeader = getAuthorizationHeader();
        try {
            var productResponse = webClient.get()
                    .uri(productUrl + "/{id}", requestDto.getProductId())
                    .header("Authorization", authHeader)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception ex) {
            log.error("Product validation failed for productId={}", requestDto.getProductId(), ex);
            throw new DataNotFoundException("Product not found");
        }

        CartEntity cart = cartRepository.findById(userId)
                .orElseGet(() -> {
                    log.info("Creating new cart for user {}", userId);
                    return CartEntity.builder()
                            .cartId(userId)
                            .items(new ArrayList<>())
                            .totalAmount(0.0)
                            .build();
                });

        cart.getItems().add(requestDto);
        cart.setTotalAmount(cart.getTotalAmount() + (requestDto.getPrice() * requestDto.getQuantity()));

        CartEntity savedCart = cartRepository.save(cart);

        log.info("Product added to cart :: userId={}, totalAmount={}",
                userId, savedCart.getTotalAmount());

        return CartResponseDto.builder()
                .cartId(cart.getCartId())
                .items(cart.getItems())
                .totalAmount(cart.getTotalAmount())
                .build();
    }

    public CartResponseDto getCart(String userId) {
        log.info("Fetching cart for user {}", userId);
        CartEntity cart = cartRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Cart not found"));
        return CartResponseDto.builder()
                .cartId(cart.getCartId())
                .items(cart.getItems())
                .totalAmount(cart.getTotalAmount())
                .build();
    }

    public String checkout(String userId) {
        log.info("Checkout initiated for user {}", userId);
        CartEntity cart = cartRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            log.warn("Checkout failed – empty cart | userId={}", userId);
            throw new BusinessException("Cart is empty");
        }

        log.info("Checkout event sent for userId={}, amount={}",
                userId, cart.getTotalAmount());
        eventProducer.sendCheckoutEvent(userId, cart.getTotalAmount());

        return "Checkout started for user " + userId;

    }

    private String getAuthorizationHeader() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs != null) {
            return attrs.getRequest().getHeader("Authorization");
        }
        return null;
    }
}
