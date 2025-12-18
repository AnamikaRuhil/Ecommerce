package com.ecommerce.advance.price.service.impl;

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

        logger.info("Inside price service");
        PriceEntity entity = PriceEntity.builder()
                .productId(requestDto.getProductId())
                .price(requestDto.getPrice())
                .build();
        entity = priceRepository.save(entity);

        return PriceResponseDto.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .price(entity.getPrice())
                .build();
    }

    @Override
    public PriceResponseDto findByProductId(Long pid) {
        PriceEntity entity = priceRepository.findByProductId(pid)
                .orElseThrow(() -> new RuntimeException("ProductPrice not prsent for product with id :{}" + pid));
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
    public void softDeleteByProductId(Long pid) {
        PriceEntity price = priceRepository.findByProductId(pid)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + pid));
        price.setDeleted(true);
        priceRepository.save(price);
    }

    @Transactional
    public void restoreProductPrice(Long pid) {
        PriceEntity price = priceRepository.findByProductId(pid)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + pid));
        price.setDeleted(false);
        priceRepository.save(price);
    }

    @Transactional
    public void hardDeleteByProductId(Long pid) {
        priceRepository.deleteByProductId(pid);
    }

    @Override
    public List<PriceResponseDto> findAll() {
      List<PriceEntity> productList = priceRepository.findAll();
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
