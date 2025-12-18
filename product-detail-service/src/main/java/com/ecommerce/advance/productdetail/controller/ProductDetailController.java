package com.ecommerce.advance.productdetail.controller;

import com.ecommerce.advance.productdetail.requestdto.ProductDetailRequestDto;
import com.ecommerce.advance.productdetail.responsedto.ProductDetailResponseDto;
import com.ecommerce.advance.productdetail.service.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productdetails")
public class ProductDetailController {
    private final ProductDetailService detailService;

    @Autowired
    public ProductDetailController(ProductDetailService detailService) {
        this.detailService = detailService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDetailResponseDto> create(@RequestBody ProductDetailRequestDto requestDto){
       ProductDetailResponseDto responseDto = detailService.create(requestDto);
       return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/get/{pid}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ProductDetailResponseDto> getByProductId(@PathVariable Long pid){
        ProductDetailResponseDto responseDto = detailService.findByProductId(pid);
        return ResponseEntity.ok(responseDto);

    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<ProductDetailResponseDto>> getAllProducts(){
        List<ProductDetailResponseDto> responseDtos = detailService.findAll();
        return ResponseEntity.ok(responseDtos);

    }

    @GetMapping("/get/category/{category}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<ProductDetailResponseDto>> getByCategory(@PathVariable String category) {
        List<ProductDetailResponseDto> response =  detailService.findByCategory(category);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{pid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteByProduct(@PathVariable Long pid){

        try {
            detailService.deleteByProductId(pid);
            return ResponseEntity.ok("✅ Product deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Product Deletion failed: " + e.getMessage());
        }
    }



}
