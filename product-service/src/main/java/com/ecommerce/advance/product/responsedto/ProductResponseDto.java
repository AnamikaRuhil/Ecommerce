package com.ecommerce.advance.product.responsedto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ProductResponseDto {
    private Long id;
    private String name;
    private String category;
    private Integer stock;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private ProductDetailResponse detailResponse; // from Product Detail Service
    private ProductPriceResponse priceResponse;  // from Price Service
}
