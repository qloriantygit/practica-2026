package ru.practica2026.notification.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.notification.dto.NotificationDeliveryPageResponse;
import ru.practica2026.notification.dto.NotificationDeliveryResponse;
import ru.practica2026.notification.entity.NotificationDelivery;
import ru.practica2026.notification.entity.NotificationStatus;
import ru.practica2026.notification.repository.NotificationDeliveryRepository;

@Service
public class NotificationQueryService {

    private final NotificationDeliveryRepository repository;

    public NotificationQueryService(
            NotificationDeliveryRepository repository
    ) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public NotificationDeliveryPageResponse findAll(
            NotificationStatus status,
            int page,
            int size
    ) {
        PageRequest pageable =
                PageRequest.of(
                        Math.max(page, 0),
                        Math.min(
                                Math.max(size, 1),
                                100
                        ),
                        Sort.by(
                                Sort.Direction.DESC,
                                "receivedAt"
                        )
                );

        Page<NotificationDelivery> result =
                status == null
                        ? repository.findAll(pageable)
                        : repository.findAllByStatus(
                                status,
                                pageable
                        );

        return new NotificationDeliveryPageResponse(
                result.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    private NotificationDeliveryResponse toResponse(
            NotificationDelivery delivery
    ) {
        return new NotificationDeliveryResponse(
                delivery.getBusinessKey(),
                delivery.getEventId(),
                delivery.getCorrelationId(),
                delivery.getEventVersion(),
                delivery.getChannel(),
                delivery.getRecipient(),
                delivery.getSubject(),
                delivery.getStatus(),
                delivery.getAttemptCount(),
                delivery.getReceivedAt(),
                delivery.getSentAt(),
                delivery.getLastError()
        );
    }
}
