package com.ecommerce.advance.price.service;

import com.ecommerce.advance.price.requestdto.PriceRequestDto;
import com.ecommerce.advance.price.responsedto.PriceResponseDto;

import java.util.List;

public interface PriceService {
    PriceResponseDto create(PriceRequestDto p);

    PriceResponseDto findByProductId(Long pid);

    void deleteByProductId(Long pid);

    List<PriceResponseDto> findAll();

    void softDeleteByProductId(Long productId);

    void hardDeleteByProductId(Long productId);

    void restoreProductPrice(Long productId);
}
