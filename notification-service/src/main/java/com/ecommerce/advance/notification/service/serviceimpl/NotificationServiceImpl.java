package com.ecommerce.advance.notification.service.serviceimpl;


import com.ecommerce.advance.notification.model.NotificationEntity;
import com.ecommerce.advance.notification.repository.NotificationRepository;
import com.ecommerce.advance.notification.requestdto.NotificationRequestDto;
import com.ecommerce.advance.notification.responsedto.NotificationResponseDto;
import com.ecommerce.advance.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
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
        NotificationEntity entity = NotificationEntity.builder()
                        .userId(requestDto.getUserId())
                .message(requestDto.getMessage())
                .eventType(requestDto.getEventType())
                .build();
        entity.setTimestamp(Instant.now());
        repository.save(entity);
    }

    @Override
    public List<NotificationResponseDto> getNotificationsByUser(String userId) {

        List<NotificationEntity>  entityList =  repository.findByUserId(userId);
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
        List<NotificationEntity>  entityList =  repository.findAll();
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