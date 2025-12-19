package com.ecommerce.advance.price.service.impl;

import com.ecommerce.advance.price.exception.DataNotFoundException;
import com.ecommerce.advance.price.model.PriceEntity;
import com.ecommerce.advance.price.repository.PriceRepository;
import com.ecommerce.advance.price.requestdto.PriceRequestDto;
import com.ecommerce.advance.price.responsedto.PriceResponseDto;
import com.ecommerce.advance.price.service.PriceService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;

    public static final Logger logger = LoggerFactory.getLogger(PriceServiceImpl.class);


    @Autowired
    public PriceServiceImpl(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @Override
    public PriceResponseDto create(PriceRequestDto requestDto) {

        log.info("Creating price | productId={}, price={}",
                requestDto.getProductId(), requestDto.getPrice());
        PriceEntity entity = PriceEntity.builder()
                .productId(requestDto.getProductId())
                .price(requestDto.getPrice())
                .build();
        PriceEntity savedEntity = priceRepository.save(entity);
        log.info("Price created | productId={}, priceId={}",
                savedEntity.getProductId(), savedEntity.getId());

        return PriceResponseDto.builder()
                .id(savedEntity.getId())
                .productId(savedEntity.getProductId())
                .price(savedEntity.getPrice())
                .build();
    }

    @Override
    public PriceResponseDto findByProductId(Long productId) {
        log.info("Fetching price for productId={}", productId);
        PriceEntity entity = priceRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    log.warn("Price not found for productId={}", productId);
                    return new DataNotFoundException(
                            "Price not found for product " + productId);
                });

        return PriceResponseDto.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .price(entity.getPrice())
                .build();
    }

    @Override
    @Transactional
    public void deleteByProductId(Long pid) {
        PriceEntity price = priceRepository.findByProductId(pid)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + pid));

        price.setDeleted(true);
        priceRepository.deleteByProductId(pid);
    }



    @Transactional
    public void softDeleteByProductId(Long productId) {
        log.info("Soft deleting price | productId={}", productId);
        PriceEntity price = priceRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Price not found for product " + productId));
        price.setDeleted(true);
        priceRepository.save(price);
        log.info("Price soft deleted | productId={}", productId);

    }

    @Transactional
    public void restoreProductPrice(Long productId) {
        log.info("Restoring price | productId={}", productId);
        PriceEntity price = priceRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Price not found for product " + productId));
        price.setDeleted(false);
        priceRepository.save(price);
        log.info("Price restored | productId={}", productId);
    }

    @Transactional
    public void hardDeleteByProductId(Long productId) {
        log.info("Hard deleting price | productId={}", productId);
        if (!priceRepository.existsByProductId(productId)) {
            log.warn("Hard delete failed – price not found | productId={}", productId);
            throw new DataNotFoundException(
                    "Price not found for product " + productId);
        }
        priceRepository.deleteByProductId(productId);
        log.info("Price permanently deleted | productId={}", productId);
    }

    @Override
    public List<PriceResponseDto> findAll() {
        log.info("Fetching all prices");
         List<PriceEntity> productList = priceRepository.findAll();
        if (productList.isEmpty()) {
            log.warn("No prices available");
            throw new DataNotFoundException("No prices found");
        }
         List<PriceResponseDto> responseList = new ArrayList<>();
         PriceResponseDto responseDto ;
         for(PriceEntity entity : productList){
          responseDto = PriceResponseDto.builder()
                  .id(entity.getId())
                  .price(entity.getPrice())
                  .productId(entity.getProductId())
                  .build();
          responseList.add(responseDto);
      }
      return  responseList;
    }

}
