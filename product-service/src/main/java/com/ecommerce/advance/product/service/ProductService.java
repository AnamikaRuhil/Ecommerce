package com.ecommerce.advance.product.service;

import com.ecommerce.advance.product.requestdto.ProductRequestDto;
import com.ecommerce.advance.product.responsedto.ProductResponseDto;

import java.util.List;

public interface ProductService {
    ProductResponseDto create(ProductRequestDto requestDto);

    void deleteById(Long id);

    List<ProductResponseDto> getAll();

    ProductResponseDto getById(Long id);

    void softDelete(Long id);

    void restore(Long productId);

    void hardDelete(Long productId);
}
