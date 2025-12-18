package com.ecommerce.advance.cart.requestdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequestDto {
    Long productId;
    private String name;
    private int quantity;
    private double price; // stored price at the time of adding

}
