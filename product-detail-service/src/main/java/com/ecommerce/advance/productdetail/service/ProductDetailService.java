package com.ecommerce.advance.productdetail.service;

import com.ecommerce.advance.productdetail.requestdto.ProductDetailRequestDto;
import com.ecommerce.advance.productdetail.responsedto.ProductDetailResponseDto;

import java.util.List;

public interface ProductDetailService {
    ProductDetailResponseDto create(ProductDetailRequestDto requestDto);

    ProductDetailResponseDto findByProductId(Long pid);

    void deleteByProductId(Long pid);

    List<ProductDetailResponseDto> findAll();

    List<ProductDetailResponseDto> findByCategory(String category);

    void softDeleteByProductId(Long productId);

    void hardDeleteByProductId(Long productId);

    void restoreByProductId(Long productId);
}
