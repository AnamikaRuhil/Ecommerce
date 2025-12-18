package com.ecommerce.advance.product.service.impl;

import com.ecommerce.advance.product.model.ProductEntity;
import com.ecommerce.advance.product.repo.ProductRepository;
import com.ecommerce.advance.product.requestdto.ProductRequestDto;
import com.ecommerce.advance.product.responsedto.ProductDetailResponse;
import com.ecommerce.advance.product.responsedto.ProductPriceResponse;
import com.ecommerce.advance.product.responsedto.ProductResponseDto;
import com.ecommerce.advance.product.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    public static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository repository;
    private final WebClient webClient = WebClient.create();


    @Value("${product.service.detail-url}")
    private String detailServiceUrl;

    @Value("${product.service.price-url}")
    private String priceServiceUrl;

    @Autowired
    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public ProductResponseDto create(ProductRequestDto requestDto) {
        logger.info("Inside product create service");
        ProductEntity entity = ProductEntity.builder()
                .name(requestDto.getName())
                .stock(requestDto.getStock())
                .category(requestDto.getCategory())
                .status(requestDto.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entity.setDeleted(false);

        entity = repository.save(entity);

        return mapToResponse(entity);

    }

    @Transactional
    public void softDelete(Long id) {
        ProductEntity product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        product.setDeleted(true);
        product.setUpdatedAt(LocalDateTime.now());
        repository.save(product);
    }

    @Transactional
    public void restore(Long id) {
        ProductEntity product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        product.setDeleted(false);
        product.setUpdatedAt(LocalDateTime.now());
        repository.save(product);
    }

    @Transactional
    public void hardDelete(Long id) {
        repository.deleteById(id);
    }


    public List<ProductResponseDto> getAll() {
        List<ProductEntity> products = repository.findAll();
        return products.stream().map(this::mapToResponseWithDetails).toList();
    }

    public ProductResponseDto getById(Long id) {
        ProductEntity product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        return mapToResponseWithDetails(product);
    }

    private ProductResponseDto mapToResponseWithDetails(ProductEntity productEntity) {
        ProductDetailResponse detail = fetchDetail(productEntity.getId());
        ProductPriceResponse price = fetchPrice(productEntity.getId());
        ProductResponseDto base = mapToResponse(productEntity);
        base.setDetailResponse(detail);
        base.setPriceResponse(price);
        return base;
    }

    private ProductPriceResponse fetchPrice(Long productId) {
        try {
            return webClient.get()
                    .uri(priceServiceUrl + "/{productId}", productId)
                    .retrieve()
                    .bodyToMono(ProductPriceResponse.class)
                    .onErrorResume(ex -> Mono.just(
                            ProductPriceResponse.builder()
                                    .error("Price not available")
                                    .build()
                    ))
                    .block();
        } catch (Exception e) {
           return ProductPriceResponse.builder()
                   .error("Failed to fetch price: " + e.getMessage())
                   .build();
        }
    }

    private ProductDetailResponse fetchDetail(Long productId) {
        try {
            return webClient.get()
                    .uri(detailServiceUrl + "/get/{pid}", productId)
                    .retrieve()
                    .bodyToMono(ProductDetailResponse.class)
                    .onErrorResume(ex -> Mono.just(
                                    ProductDetailResponse.builder()
                                            .attributes(Map.of("error", "Details not available"))
                                            .build()))
                    .block();
        } catch (Exception e) {
            return ProductDetailResponse.builder()
                    .attributes(Map.of("error", "Failed to fetch Details    Q2"))
                    .build();
        }
    }

    private ProductResponseDto mapToResponse(ProductEntity entity) {
        return ProductResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .stock(entity.getStock())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public void deleteById(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        repository.deleteById(id);

    }
}
