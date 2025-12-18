package com.ecommerce.advance.cart.responsedto;


import com.ecommerce.advance.cart.requestdto.CartRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDto {

    private String cartId;  // userId = cartId

    private List<CartRequestDto> items;

    private double totalAmount;

}
