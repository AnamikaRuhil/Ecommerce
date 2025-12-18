package com.ecommerce.advance.product.contoller;

import com.ecommerce.advance.common.events.ProductEvent;
import com.ecommerce.advance.product.requestdto.ProductRequestDto;
import com.ecommerce.advance.product.responsedto.ProductResponseDto;
import com.ecommerce.advance.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(ProductService productService, KafkaTemplate<String, ProductEvent> kafkaTemplate) {
        this.productService = productService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDto> create(@RequestBody ProductRequestDto requestDto) {
        ProductResponseDto responseDto = productService.create(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        productService.softDelete(id);
        ProductEvent event = ProductEvent.builder()
                .productId(id)
                .eventType("PRODUCT_DELETED")
                .source("product-service")
                .message("Product soft deleted")
                .build();
        kafkaTemplate.send("product-events", event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("EVENT SENT SUCCESSFULLY → " + result.getRecordMetadata());
                    } else {
                        System.out.println("FAILED TO SEND EVENT → " + ex.getMessage());
                    }
                });
    }
}
