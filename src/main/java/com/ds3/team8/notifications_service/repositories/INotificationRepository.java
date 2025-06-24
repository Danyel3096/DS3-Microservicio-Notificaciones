package com.ds3.team8.notifications_service.repositories;

import com.ds3.team8.notifications_service.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByIdAndIsActiveTrue(Long id); // Obtener notificación por ID y activa
    List<Notification> findAllByCustomerIdAndIsActiveTrue(Long customerId); // Obtener notificaciones por ID de cliente y activas
    Page<Notification> findAllByCustomerIdAndIsActiveTrue(Long customerId, Pageable pageable); // Obtener notificaciones por ID de cliente y activas con paginación
    List<Notification> findAllByIsActiveTrue(); // Obtener todas las notificaciones activas
    Page<Notification> findAllByIsActiveTrue(Pageable pageable); // Obtener todas las notificaciones activas con paginación
}
