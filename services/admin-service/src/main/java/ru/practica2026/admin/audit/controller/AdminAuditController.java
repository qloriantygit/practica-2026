package ru.practica2026.admin.audit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practica2026.admin.audit.dto.AdminAuditLogPageResponse;
import ru.practica2026.admin.audit.service.AdminAuditService;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AdminAuditController {

    private final AdminAuditService service;

    public AdminAuditController(
            AdminAuditService service
    ) {
        this.service = service;
    }

    @GetMapping
    public AdminAuditLogPageResponse findAll(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            Boolean success,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "50")
            int size
    ) {
        return service.findAll(
                search,
                success,
                page,
                size
        );
    }
}
