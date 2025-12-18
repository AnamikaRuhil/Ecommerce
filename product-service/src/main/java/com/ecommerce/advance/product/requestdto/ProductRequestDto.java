package com.ecommerce.advance.product.requestdto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductRequestDto {
    private String name;
    private String category;
    private Integer stock;
    private String status;
}
