package com.ecommerce.advance.price.controller;

import com.ecommerce.advance.price.requestdto.PriceRequestDto;
import com.ecommerce.advance.price.responsedto.PriceResponseDto;
import com.ecommerce.advance.price.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/price/product")
public class PriceController {
    private final PriceService priceService;

    @Autowired
    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PriceResponseDto> create(@RequestBody PriceRequestDto requestDto){
       PriceResponseDto responseDto = priceService.create(requestDto);
       return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{pid}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PriceResponseDto> getByProductId(@PathVariable Long pid){
        PriceResponseDto responseDto = priceService.findByProductId(pid);
        return ResponseEntity.ok(responseDto);

    }
    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<PriceResponseDto>> getAllProducts(){
        List<PriceResponseDto> responseDtos = priceService.findAll();
        return ResponseEntity.ok(responseDtos);

    }

    @DeleteMapping("/delete/{pid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteByProduct(@PathVariable Long pid){

        try {
            priceService.deleteByProductId(pid);
            return ResponseEntity.ok("✅ Product deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Product Deletion failed: " + e.getMessage());
        }
    }

}
