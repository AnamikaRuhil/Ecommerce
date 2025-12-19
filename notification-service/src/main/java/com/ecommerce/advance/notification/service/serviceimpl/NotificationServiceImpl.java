package com.ecommerce.advance.notification.service.serviceimpl;


import com.ecommerce.advance.notification.exception.BusinessException;
import com.ecommerce.advance.notification.exception.DataNotFoundException;
import com.ecommerce.advance.notification.model.NotificationEntity;
import com.ecommerce.advance.notification.repository.NotificationRepository;
import com.ecommerce.advance.notification.requestdto.NotificationRequestDto;
import com.ecommerce.advance.notification.responsedto.NotificationResponseDto;
import com.ecommerce.advance.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveNotification(NotificationRequestDto requestDto) {
        log.info("Saving notification : userId={}, eventType={}",
                requestDto.getUserId(), requestDto.getEventType());
        try {
            NotificationEntity entity = NotificationEntity.builder()
                    .userId(requestDto.getUserId())
                    .message(requestDto.getMessage())
                    .eventType(requestDto.getEventType())
                    .build();
            entity.setTimestamp(Instant.now());
            repository.save(entity);
            log.info("Notification saved successfully | userId={}", requestDto.getUserId());
        }
     catch (Exception ex) {
        log.error("Failed to save notification for userId={}", requestDto.getUserId(), ex);
        throw new BusinessException("Failed to save notification");
    }
    }

    @Override
    public List<NotificationResponseDto> getNotificationsByUser(String userId) {
        log.info("Fetching notifications for user {}", userId);

        List<NotificationEntity>  entityList =  repository.findByUserId(userId);
        if (entityList.isEmpty()) {
            log.warn("No notifications found for user {}", userId);
            throw new DataNotFoundException("No notifications found for user");
        }
        List<NotificationResponseDto> responses = new ArrayList<>();
        for(NotificationEntity entity : entityList) {
            NotificationResponseDto response = NotificationResponseDto.builder()
                    .id(entity.getId())
                    .userId(entity.getUserId())
                    .message(entity.getMessage())
                    .eventType(entity.getEventType())
                    .timestamp(entity.getTimestamp())
                    .build();
            responses.add(response);
        }
        return responses;
    }

    @Override
    public List<NotificationResponseDto> getAllNotifications() {
        log.info("Fetching all notifications");
        List<NotificationEntity>  entityList =  repository.findAll();
        if (entityList.isEmpty()) {
            log.warn("No notifications found");
            throw new DataNotFoundException("No notifications available");
        }
        List<NotificationResponseDto> responses = new ArrayList<>();
        for(NotificationEntity entity : entityList) {
            NotificationResponseDto response = NotificationResponseDto.builder()
                    .id(entity.getId())
                    .userId(entity.getUserId())
                    .message(entity.getMessage())
                    .eventType(entity.getEventType())
                    .timestamp(entity.getTimestamp())
                    .build();
            responses.add(response);
        }
        return responses;
    }
}