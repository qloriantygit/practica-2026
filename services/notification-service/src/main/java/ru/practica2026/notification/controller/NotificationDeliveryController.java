package ru.practica2026.notification.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practica2026.notification.dto.NotificationDeliveryPageResponse;
import ru.practica2026.notification.entity.NotificationStatus;
import ru.practica2026.notification.service.NotificationQueryService;

@RestController
@RequestMapping(
        "/api/v1/notification-deliveries"
)
public class NotificationDeliveryController {

    private final NotificationQueryService service;

    public NotificationDeliveryController(
            NotificationQueryService service
    ) {
        this.service = service;
    }

    @GetMapping
    public NotificationDeliveryPageResponse findAll(
            @RequestParam(required = false)
            NotificationStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        return service.findAll(
                status,
                page,
                size
        );
    }
}
