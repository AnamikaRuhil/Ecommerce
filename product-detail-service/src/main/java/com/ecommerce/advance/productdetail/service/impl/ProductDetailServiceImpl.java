package com.ecommerce.advance.productdetail.service.impl;

import com.ecommerce.advance.productdetail.exception.DataNotFoundException;
import com.ecommerce.advance.productdetail.model.ProductDetailEntity;
import com.ecommerce.advance.productdetail.repository.ProductDetailRepository;
import com.ecommerce.advance.productdetail.requestdto.ProductDetailRequestDto;
import com.ecommerce.advance.productdetail.responsedto.ProductDetailResponseDto;
import com.ecommerce.advance.productdetail.service.ProductDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProductDetailServiceImpl implements ProductDetailService {
    private final ProductDetailRepository detailRepository;

    @Autowired
    public ProductDetailServiceImpl(ProductDetailRepository detailRepository) {
        this.detailRepository = detailRepository;
    }

    @Override
    public ProductDetailResponseDto create(ProductDetailRequestDto requestDto) {
        log.info("Creating product detail | productId={}, category={}, brand={}",
                requestDto.getProductId(), requestDto.getCategory(), requestDto.getBrand());

        ProductDetailEntity entity = ProductDetailEntity.builder()
                .productId(requestDto.getProductId())
                .category(requestDto.getCategory())
                .brand(requestDto.getBrand())
                .attributes(requestDto.getAttributes())
                .build();
        ProductDetailEntity savedEntity = detailRepository.save(entity);
        log.info("Product detail created | productId={}, detailId={}",
                savedEntity.getProductId(), savedEntity.getId());

        return mapToDto(savedEntity);
    }

    @Override
    public ProductDetailResponseDto findByProductId(Long productId) {
        log.info("Fetching product detail | productId={}", productId);
        ProductDetailEntity entity = detailRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    log.warn("Product detail not found | productId={}", productId);
                    return new DataNotFoundException(
                            "Product detail not found for product " + productId);
                });

        return mapToDto(entity);
    }

    @Override
    public void deleteByProductId(Long productId) {
        log.info("Deleting product detail | productId={}", productId);
        if (!detailRepository.existsByProductId(productId)) {
            log.warn("Delete failed – product detail not found | productId={}", productId);
            throw new DataNotFoundException(
                    "Product detail not found for product " + productId);
        }
        detailRepository.deleteByProductId(productId);
        log.info("Product detail deleted | productId={}", productId);

    }

    @Override
    public List<ProductDetailResponseDto> findAll() {
        log.info("Fetching all product details");

        List<ProductDetailEntity> entities = detailRepository.findAll();

        if (entities.isEmpty()) {
            log.warn("No product details available");
            throw new DataNotFoundException("No product details found");
        }

        return entities.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<ProductDetailResponseDto> findByCategory(String category) {
        log.info("Fetching product details by category={}", category);

        List<ProductDetailEntity> entities = detailRepository.findByCategory(category);

        if (entities.isEmpty()) {
            log.warn("No product details found for category={}", category);
            throw new DataNotFoundException(
                    "No product details found for category " + category);
        }

        return entities.stream()
                .map(this::mapToDto)
                .toList();
    }


    @Transactional
    public void softDeleteByProductId(Long productId) {
        log.info("Soft deleting product detail | productId={}", productId);

        ProductDetailEntity entity = detailRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Product detail not found for product " + productId));

        entity.setDeleted(true);
        detailRepository.save(entity);

        log.info("Product detail soft deleted | productId={}", productId);
    }

    @Transactional
    public void restoreByProductId(Long productId) {
        log.info("Restoring product detail | productId={}", productId);
        ProductDetailEntity entity = detailRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Product detail not found for product " + productId));
        entity.setDeleted(false);
        detailRepository.save(entity);

        log.info("Product detail restored | productId={}", productId);
    }

    @Transactional
    public void hardDeleteByProductId(Long productId) {
        log.info("Hard deleting product detail | productId={}", productId);

        if (!detailRepository.existsByProductId(productId)) {
            log.warn("Hard delete failed – product detail not found | productId={}", productId);
            throw new DataNotFoundException(
                    "Product detail not found for product " + productId);
        }

        detailRepository.deleteByProductId(productId);

        log.info("Product detail permanently deleted | productId={}", productId);

    }

    private ProductDetailResponseDto mapToDto(ProductDetailEntity entity) {
        return ProductDetailResponseDto.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .category(entity.getCategory())
                .brand(entity.getBrand())
                .attributes(entity.getAttributes())
                .build();
    }
}
