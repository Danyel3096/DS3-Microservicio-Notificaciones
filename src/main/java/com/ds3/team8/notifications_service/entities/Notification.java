package com.ds3.team8.notifications_service.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // Genera automáticamente getters, setters, equals, hashCode y toString
@NoArgsConstructor  // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
@Entity  // Indica que esta clase es una entidad JPA
@Table(name = "notifications")  // Nombre de la tabla en la base de datos
public class Notification {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Autoincremental
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId; // ID del cliente

    @Column(name = "order_id", nullable = false)
    private Long orderId; // ID del pedido

    @Column(nullable = false)
    private String message; // Mensaje de la notificación

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false; // Indica si la notificación ha sido leída

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @PreUpdate
    public void setLastUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }


    public Notification(Long customerId, Long orderId, String message) {
        this.customerId = customerId;
        this.orderId = orderId;
        this.message = message;
    }
}
