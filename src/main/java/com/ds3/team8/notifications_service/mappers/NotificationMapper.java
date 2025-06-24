package com.ds3.team8.notifications_service.mappers;

import com.ds3.team8.notifications_service.dtos.NotificationRequest;
import com.ds3.team8.notifications_service.dtos.NotificationResponse;
import com.ds3.team8.notifications_service.entities.Notification;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapper {
    public NotificationResponse toNotificationResponse(Notification notification) {
        if (notification == null) return null;

        return new NotificationResponse(
                notification.getId(),
                notification.getCustomerId(),
                notification.getOrderId(),
                notification.getMessage(),
                notification.getIsRead(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }

    public Notification toNotification(NotificationRequest request) {
        if (request == null) return null;

        return new Notification(
                request.getCustomerId(),
                request.getOrderId(),
                request.getMessage()
        );
    }

    public List<NotificationResponse> toNotificationList(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) return List.of();

        return notifications.stream()
                .map(this::toNotificationResponse)
                .collect(Collectors.toList());
    }
}
