package com.ecommerce.advance.productdetail.service.impl;

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
        ProductDetailEntity entity = ProductDetailEntity.builder()
                .productId(requestDto.getProductId())
                .category(requestDto.getCategory())
                .brand(requestDto.getBrand())
                .attributes(requestDto.getAttributes())
                .build();
        entity = detailRepository.save(entity);

        return ProductDetailResponseDto.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .category(entity.getCategory())
                .brand(entity.getBrand())
                .attributes(entity.getAttributes())
                .build();
    }

    @Override
    public ProductDetailResponseDto findByProductId(Long pid) {
        ProductDetailResponseDto response;
        ProductDetailEntity entity = detailRepository.findByProductId(pid).
                orElseThrow(() -> new RuntimeException("Product data not found for ID: " + pid));

        response = ProductDetailResponseDto.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .category(entity.getCategory())
                .brand(entity.getBrand())
                .attributes(entity.getAttributes())
                .build();

        return response;
    }

    @Override
    public void deleteByProductId(Long pid) {
        detailRepository.findByProductId(pid)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + pid));
        detailRepository.deleteByProductId(pid);

    }

    @Override
    public List<ProductDetailResponseDto> findAll() {
        List<ProductDetailEntity> productList = detailRepository.findAll();
        List<ProductDetailResponseDto> responseList = new ArrayList<>();
        ProductDetailResponseDto responseDto ;
        for(ProductDetailEntity entity : productList){
            responseDto = ProductDetailResponseDto.builder()
                    .id(entity.getId())
                    .productId(entity.getProductId())
                    .category(entity.getCategory())
                    .brand(entity.getBrand())
                    .attributes(entity.getAttributes())
                    .build();
            responseList.add(responseDto);
        }
        return  responseList;
    }

    @Override
    public List<ProductDetailResponseDto> findByCategory(String category) {
        List<ProductDetailEntity> productList = detailRepository.findByCategory(category);
        List<ProductDetailResponseDto> responseList = new ArrayList<>();
        ProductDetailResponseDto responseDto ;
        for(ProductDetailEntity entity : productList){
            responseDto = ProductDetailResponseDto.builder()
                    .id(entity.getId())
                    .productId(entity.getProductId())
                    .category(entity.getCategory())
                    .brand(entity.getBrand())
                    .attributes(entity.getAttributes())
                    .build();
            responseList.add(responseDto);
        }
        return responseList;
    }



    @Transactional
    public void softDeleteByProductId(Long pid) {
      ProductDetailEntity detailEntity =  detailRepository.findByProductId(pid)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + pid));

        detailEntity.setDeleted(true);
        detailRepository.save(detailEntity);
    }

    @Transactional
    public void restoreByProductId(Long pid) {
        ProductDetailEntity detailEntity =  detailRepository.findByProductId(pid)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + pid));

        detailEntity.setDeleted(false);
        detailRepository.save(detailEntity);
    }

    @Transactional
    public void hardDeleteByProductId(Long pid) {
        detailRepository.deleteByProductId(pid);
    }
}
