package com.ecommerce.advance.notification.service;



import com.ecommerce.advance.notification.requestdto.NotificationRequestDto;
import com.ecommerce.advance.notification.responsedto.NotificationResponseDto;

import java.util.List;

public interface NotificationService {

    void saveNotification(NotificationRequestDto notification);

    List<NotificationResponseDto> getNotificationsByUser(String userId);

    List<NotificationResponseDto> getAllNotifications();
}