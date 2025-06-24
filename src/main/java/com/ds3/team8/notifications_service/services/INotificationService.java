package com.ds3.team8.notifications_service.services;

import com.ds3.team8.notifications_service.dtos.NotificationRequest;
import com.ds3.team8.notifications_service.dtos.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface INotificationService {
    List<NotificationResponse> findAll(); // Obtener todas las notificaciones
    NotificationResponse save(NotificationRequest notificationRequest); // Crear una notificación
    Page<NotificationResponse> findAllPageable(Pageable pageable); // Obtener todas las notificaciones con paginación
    NotificationResponse findById(Long id); // Obtener una notificación por su ID
    List<NotificationResponse> findAllByCustomerId(Long customerId); // Obtener notificaciones por ID de cliente
    Page<NotificationResponse> findAllByCustomerId(Long customerId, Pageable pageable); // Obtener notificaciones por ID de cliente con paginación
    NotificationResponse markAsRead(Long id); // Marcar una notificación como leída
}
