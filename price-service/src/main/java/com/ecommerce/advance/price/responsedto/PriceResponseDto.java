package com.ecommerce.advance.price.responsedto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceResponseDto {
    private Long id;
    private Double price;
    private Long productId;

}
