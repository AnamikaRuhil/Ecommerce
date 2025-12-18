package com.ecommerce.advance.productdetail.responsedto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ProductDetailResponseDto {
    private String id;
    private Long productId;
    private String category;
    private String brand;
    private Map<String, Object> attributes;
}
