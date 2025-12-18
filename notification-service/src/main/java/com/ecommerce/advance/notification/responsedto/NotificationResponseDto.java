package com.ecommerce.advance.notification.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private String id;

    private String userId;
    private String eventType;
    private String message;
    private Instant timestamp;
}
