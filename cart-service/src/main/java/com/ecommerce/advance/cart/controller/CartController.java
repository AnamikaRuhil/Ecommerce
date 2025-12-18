package com.ecommerce.advance.cart.controller;

import com.ecommerce.advance.cart.requestdto.CartRequestDto;
import com.ecommerce.advance.cart.responsedto.CartResponseDto;
import com.ecommerce.advance.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService){
        this.cartService = cartService;
    }

    @PostMapping("/{userId}/add")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CartResponseDto addToCart(@PathVariable String userId,
                                     @RequestBody CartRequestDto requestDto) {
        return cartService.addToCart(userId, requestDto);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CartResponseDto viewCart(@PathVariable String userId) {
        return cartService.getCart(userId);
    }

    @PostMapping("/{userId}/checkout")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String checkout(@PathVariable String userId) {
        return cartService.checkout(userId);
    }
}
