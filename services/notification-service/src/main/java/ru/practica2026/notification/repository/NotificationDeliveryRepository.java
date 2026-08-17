package ru.practica2026.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practica2026.notification.entity.NotificationDelivery;
import ru.practica2026.notification.entity.NotificationStatus;

import java.util.Optional;
import java.util.UUID;

public interface NotificationDeliveryRepository
        extends JpaRepository<
                NotificationDelivery,
                Long
                > {

    Optional<NotificationDelivery> findByEventId(
            UUID eventId
    );

    Page<NotificationDelivery> findAllByStatus(
            NotificationStatus status,
            Pageable pageable
    );
}
