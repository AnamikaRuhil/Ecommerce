package com.ecommerce.advance.notification.repository;

import com.ecommerce.advance.notification.model.NotificationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<NotificationEntity, String> {
    List<NotificationEntity> findByUserId(String userId);
}
