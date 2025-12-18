package com.ecommerce.advance.productdetail.requestdto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ProductDetailRequestDto {
    private String id;
    private Long productId;
    private String category;
    private String brand;
    private Map<String, Object> attributes;
}
