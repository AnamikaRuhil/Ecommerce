package com.ecommerce.advance.cart.service;

import com.ecommerce.advance.cart.requestdto.CartRequestDto;
import com.ecommerce.advance.cart.responsedto.CartResponseDto;

public interface CartService {
    CartResponseDto addToCart(String userId, CartRequestDto requestDto);

    CartResponseDto getCart(String userId);

    String checkout(String userId);
}
