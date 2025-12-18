package com.ecommerce.advance.price.repository;

import com.ecommerce.advance.price.model.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<PriceEntity,Long> {

    Optional<PriceEntity> findByProductId(Long pid);

    void deleteByProductId(Long pid);
}
