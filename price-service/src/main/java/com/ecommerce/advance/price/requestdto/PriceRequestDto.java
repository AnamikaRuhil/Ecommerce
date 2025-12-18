package com.ecommerce.advance.price.requestdto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceRequestDto {
    private Long id;
    private Double price;
    private Long productId;
}
