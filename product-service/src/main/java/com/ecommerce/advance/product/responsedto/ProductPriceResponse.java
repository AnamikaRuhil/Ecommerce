package com.ecommerce.advance.product.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPriceResponse {
    private Double price;
    private Long productId;
    private String error;
}
