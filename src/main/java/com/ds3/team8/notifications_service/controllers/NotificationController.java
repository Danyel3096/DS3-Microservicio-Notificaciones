package com.ds3.team8.notifications_service.controllers;

import com.ds3.team8.notifications_service.dtos.NotificationRequest;
import com.ds3.team8.notifications_service.dtos.NotificationResponse;
import com.ds3.team8.notifications_service.services.INotificationService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ds3.team8.notifications_service.utils.SecurityUtil;
import com.ds3.team8.notifications_service.client.enums.Role;

import java.util.List;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/v1/notifications") // Indica la URL base para acceder a los servicios de esta clase
@Tag(name = "Notificaciones", description = "Endpoints para notificaciones")
public class NotificationController {

    private final INotificationService notificationService;

    public NotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Obtener todas las notificaciones
    @Operation(summary = "Obtener todas las notificaciones", description = "Obtener todas las notificaciones del sistema.", security = { @SecurityRequirement(name = "Bearer Authentication") })
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(
        @RequestHeader("X-Authenticated-User-Role") String roleHeader
    ) {
        SecurityUtil.validateRole(roleHeader, Role.ADMIN);
        return ResponseEntity.ok(notificationService.findAll());
    }

    // Crear una notificación
    @Hidden
    @PostMapping
    public ResponseEntity<NotificationResponse> saveNotification(
            @Valid @RequestBody NotificationRequest notificationRequest
    ) {
        NotificationResponse savedNotification = notificationService.save(notificationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNotification);
    }

    // Buscar notificaciones con paginación
    // Ejemplo URL /api/v1/notifications/pageable?page=0&size=8
    @Operation(summary = "Obtener las notificaciones con paginación", description = "Obtener las notificaciones con paginación del sistema.", security = { @SecurityRequirement(name = "Bearer Authentication") })
    @GetMapping("/pageable")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsPageable(
        Pageable pageable,
        @RequestHeader("X-Authenticated-User-Role") String roleHeader
    ) {
        SecurityUtil.validateRole(roleHeader, Role.ADMIN);
        return ResponseEntity.ok(notificationService.findAllPageable(pageable));
    }

    // Buscar notificaciones por ID
    @Operation(summary = "Obtener una notificación por ID", description = "Obtener una notificación por su ID.", security = { @SecurityRequirement(name = "Bearer Authentication") })
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        NotificationResponse notification = notificationService.findById(id);
        return ResponseEntity.ok(notification);
    }

    // Marcar una notificación como leída
    @Operation(summary = "Marcar una notificación como leída", description = "Marcar una notificación como leída por su ID.", security = { @SecurityRequirement(name = "Bearer Authentication") })
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
        NotificationResponse notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }

    // Obtener notificaciones de un cliente específico
    @Operation(summary = "Obtener notificaciones de un cliente", description = "Obtener todas las notificaciones de un cliente específico por su ID.", security = { @SecurityRequirement(name = "Bearer Authentication") })
    @GetMapping("/user")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByCustomerId(
        @RequestHeader("X-Authenticated-User-Id") String userIdHeader
    ) {
        Long userId = SecurityUtil.parseUserId(userIdHeader);
        List<NotificationResponse> notifications = notificationService.findAllByCustomerId(userId);
        return ResponseEntity.ok(notifications);
    }

    // Obtener notificaciones de un cliente con paginación
    @Operation(summary = "Obtener notificaciones de un cliente con paginación", description = "Obtener las notificaciones de un cliente específico por su ID con paginación.", security = { @SecurityRequirement(name = "Bearer Authentication") })
    @GetMapping("/user/pageable")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByCustomerIdPageable(
            @RequestHeader("X-Authenticated-User-Id") String userIdHeader,
            Pageable pageable
    ) {
        Long userId = SecurityUtil.parseUserId(userIdHeader);
        Page<NotificationResponse> notifications = notificationService.findAllByCustomerId(userId, pageable);
        return ResponseEntity.ok(notifications);
    }
}
