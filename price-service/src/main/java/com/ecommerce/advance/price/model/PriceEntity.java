package com.ecommerce.advance.price.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Price")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PriceEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private Double price;
    private Long productId;

    private boolean deleted;
}
