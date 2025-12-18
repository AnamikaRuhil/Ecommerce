package com.ecommerce.advance.productdetail.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

@Document(collection = "product_details")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDetailEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private Long productId;
    private String category;
    private String brand;
    private Map<String, Object> attributes;
    private boolean deleted;

}
