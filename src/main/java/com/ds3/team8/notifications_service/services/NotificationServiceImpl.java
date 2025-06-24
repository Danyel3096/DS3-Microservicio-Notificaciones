package com.ds3.team8.notifications_service.services;

import com.ds3.team8.notifications_service.client.OrderClient;
import com.ds3.team8.notifications_service.client.UserClient;
import com.ds3.team8.notifications_service.dtos.NotificationRequest;
import com.ds3.team8.notifications_service.dtos.NotificationResponse;
import com.ds3.team8.notifications_service.entities.Notification;
import com.ds3.team8.notifications_service.exceptions.NotFoundException;
import com.ds3.team8.notifications_service.mappers.NotificationMapper;
import com.ds3.team8.notifications_service.repositories.INotificationRepository;

import feign.FeignException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements INotificationService {
    private final INotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final OrderClient orderClient;
    private final UserClient userClient;

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    public NotificationServiceImpl(INotificationRepository notificationRepository, NotificationMapper notificationMapper, OrderClient orderClient, UserClient userClient) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.orderClient = orderClient;
        this.userClient = userClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> findAll() {
        // Obtener todas las notificaciones activas
        List<Notification> notifications = notificationRepository.findAllByIsActiveTrue();
        if (notifications.isEmpty()) {
            logger.warn("No se encontraron notificaciones activas");
            throw new NotFoundException("No se encontraron notificaciones activas");
        }
        // Mapear a DTOs
        logger.info("Número de notificaciones activas encontradas: {}", notifications.size());
        return notificationMapper.toNotificationList(notifications);
    }

    @Override
    @Transactional
    public NotificationResponse save(NotificationRequest notificationRequest) {
        // Validar que el pedido existe
        validateOrder(notificationRequest.getOrderId());
        // Validar que el usuario existe
        validateUser(notificationRequest.getCustomerId());

        // Mapear a entidad
        Notification notification = notificationMapper.toNotification(notificationRequest);
        // Guardar la notificación
        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Notificación creada con ID: {}", savedNotification.getId());
        return notificationMapper.toNotificationResponse(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> findAllPageable(Pageable pageable) {
        // Obtener todas las notificaciones con paginación
        Page<Notification> notificationPage = notificationRepository.findAllByIsActiveTrue(pageable);
        if (notificationPage.isEmpty()) {
            logger.warn("No se encontraron notificaciones activas");
            throw new NotFoundException("No se encontraron notificaciones activas");
        }
        logger.info("Número de notificaciones activas encontradas: {}", notificationPage.getTotalElements());
        return notificationPage.map(notificationMapper::toNotificationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse findById(Long id) {
        // Buscar la notificación por ID
        Optional<Notification> optionalNotification = notificationRepository.findById(id);
        if (optionalNotification.isEmpty()) {
            logger.error("Notificación con ID {} no encontrada", id);
            throw new NotFoundException("Notificación no encontrada");
        }
        logger.info("Notificación encontrada con ID: {}", id);
        return notificationMapper.toNotificationResponse(optionalNotification.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> findAllByCustomerId(Long customerId) {
        // Validar que el usuario existe
        validateUser(customerId);
        // Obtener notificaciones por ID de cliente
        List<Notification> notifications = notificationRepository.findAllByCustomerIdAndIsActiveTrue(customerId);
        if (notifications.isEmpty()) {
            logger.warn("No se encontraron notificaciones para el cliente con ID: {}", customerId);
            throw new NotFoundException("No se encontraron notificaciones para el cliente");
        }
        logger.info("Número de notificaciones encontradas para el cliente con ID {}: {}", customerId, notifications.size());
        return notificationMapper.toNotificationList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> findAllByCustomerId(Long customerId, Pageable pageable) {
        // Validar que el usuario existe
        validateUser(customerId);
        // Obtener notificaciones por ID de cliente con paginación
        Page<Notification> notificationPage = notificationRepository.findAllByCustomerIdAndIsActiveTrue(customerId, pageable);
        if (notificationPage.isEmpty()) {
            logger.warn("No se encontraron notificaciones para el cliente con ID: {}", customerId);
            throw new NotFoundException("No se encontraron notificaciones para el cliente");
        }
        logger.info("Número de notificaciones encontradas para el cliente con ID {}: {}", customerId, notificationPage.getTotalElements());
        return notificationPage.map(notificationMapper::toNotificationResponse);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long id) {
        // Buscar la notificación por ID
        Optional<Notification> optionalNotification = notificationRepository.findById(id);
        if (optionalNotification.isEmpty()) {
            logger.error("Notificación con ID {} no encontrada para marcar como leída", id);
            throw new NotFoundException("Notificación no encontrada");
        }
        // Marcar como leída
        Notification notification = optionalNotification.get();
        notification.setIsRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        logger.info("Notificación con ID {} marcada como leída", updatedNotification.getId());
        return notificationMapper.toNotificationResponse(updatedNotification);
    }

    
    private void validateUser(Long userId) {
        try {
            userClient.getUserById(userId);
            logger.info("Usuario con ID {} validado correctamente", userId);
        } catch (FeignException e) {
            logger.error("Error al validar el usuario: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo validar el usuario", e);
        }
    }

    private void validateOrder(Long orderId) {
        try {
            orderClient.getOrderById(orderId);
            logger.info("Pedido con ID {} validado correctamente", orderId);
        } catch (FeignException e) {
            logger.error("Error al validar el pedido: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo validar el pedido", e);
        }
    }
}
