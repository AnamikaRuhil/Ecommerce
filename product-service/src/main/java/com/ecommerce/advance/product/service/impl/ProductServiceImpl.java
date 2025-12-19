package com.ecommerce.advance.product.service.impl;

import com.ecommerce.advance.product.exception.DataNotFoundException;
import com.ecommerce.advance.product.model.ProductEntity;
import com.ecommerce.advance.product.repo.ProductRepository;
import com.ecommerce.advance.product.requestdto.ProductRequestDto;
import com.ecommerce.advance.product.responsedto.ProductDetailResponse;
import com.ecommerce.advance.product.responsedto.ProductPriceResponse;
import com.ecommerce.advance.product.responsedto.ProductResponseDto;
import com.ecommerce.advance.product.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
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
        log.info("Creating product | productName={}, category={}",
                requestDto.getName(), requestDto.getCategory());

        ProductEntity entity = ProductEntity.builder()
                .name(requestDto.getName())
                .stock(requestDto.getStock())
                .category(requestDto.getCategory())
                .status(requestDto.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entity.setDeleted(false);

        ProductEntity savedEntity = repository.save(entity);
        log.info("Product created | productId={} ,productName={},",
                savedEntity.getId(), savedEntity.getName());

        return mapToResponse(savedEntity);

    }

    @Transactional
    public void softDelete(Long id) {
        log.info("Soft deleting product | product id = {}", id);
        ProductEntity product = repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product not found: " + id));

        product.setDeleted(true);
        product.setUpdatedAt(LocalDateTime.now());
        repository.save(product);
        log.info("Product soft deleted | product id = {}", id);
    }

    @Transactional
    public void restore(Long id) {
        log.info("Restoring product | product id = {}", id);
        ProductEntity product = repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product not found: " + id));
        product.setDeleted(false);
        product.setUpdatedAt(LocalDateTime.now());
        repository.save(product);
        log.info("Product Restored| product id = {}", id);
    }

    @Transactional
    public void hardDelete(Long id) {
        log.info("Hard deleting product id={}", id);
        if (!repository.existsById(id)) {
            throw new DataNotFoundException("Product not found: " + id);
        }
        repository.deleteById(id);
        log.info("Product deleted permanently | product id={}", id);
    }


    public List<ProductResponseDto> getAll() {
        log.info("Fetching all products");
        List<ProductEntity> products = repository.findAll();
        if (products.isEmpty()) {
            log.warn("No product details available");
            throw new DataNotFoundException("No product details found");
        }
        return products.stream().map(this::mapToResponseWithDetails).toList();
    }

    public ProductResponseDto getById(Long productId) {
        log.info("Fetching product by product Id={}", productId);

        ProductEntity product = repository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found | productId={}", productId);
                    return new DataNotFoundException(
                            "Product not found for Id = " + productId);
                });
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
        log.info("Fetching Price for productId={}", productId);
        String authHeader = getAuthorizationHeader();

        try {
            ProductPriceResponse response = webClient.get()
                    .uri(priceServiceUrl + "/{productId}", productId)
                    .header("Authorization", authHeader)
                    .retrieve()
                    .bodyToMono(ProductPriceResponse.class)
                    .block();
            log.info("Received Price response for productId={}", productId);

            return response;

        } catch (Exception ex) {
            log.error("Failed to fetch price details for productId={}", productId, ex);
            throw new ResourceNotFoundException("Unable to fetch product details");
        }
    }

    private ProductDetailResponse fetchDetail(Long productId) {
        log.info("Calling Product-Detail service for productId={}", productId);
        String authHeader = getAuthorizationHeader();
        try {
            ProductDetailResponse response = webClient.get()
                    .uri(detailServiceUrl + "/get/{pid}", productId)
                    .header("Authorization", authHeader)
                    .retrieve()
                    .bodyToMono(ProductDetailResponse.class)
                    .block();

            log.info("Received Product-Detail response for productId={}", productId);
            return response;

        } catch (Exception ex) {
            log.error("Failed to fetch product details for productId={}", productId, ex);
            throw new DataNotFoundException("Unable to fetch product details");
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
        log.info("Inside product delete,delete product for id {}", id);
        repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product not found with ID: " + id));
        repository.deleteById(id);

    }

    private String getAuthorizationHeader() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs != null) {
            return attrs.getRequest().getHeader("Authorization");
        }
        return null;
    }
}
