package com.ecommerce.advance.productdetail.repository;

import com.ecommerce.advance.productdetail.model.ProductDetailEntity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailRepository extends MongoRepository<ProductDetailEntity,Long> {
    Optional<ProductDetailEntity> findByProductId(Long productId);

    void deleteByProductId(Long pid);

    List<ProductDetailEntity> findByCategory(String category);
}
