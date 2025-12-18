package com.ecommerce.advance.cart.model;

import com.ecommerce.advance.cart.requestdto.CartRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "carts")
public class CartEntity {
    @Id
    private String cartId;  // userId = cartId

    private List<CartRequestDto> items;

    private double totalAmount;
}
