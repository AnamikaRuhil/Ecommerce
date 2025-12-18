package com.ecommerce.advance.cart.repository;

import com.ecommerce.advance.cart.model.CartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<CartEntity, String> {
}
