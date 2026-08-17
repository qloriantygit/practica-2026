package ru.practica2026.admin.audit.dto;

import java.util.List;

public record AdminAuditLogPageResponse(

        List<AdminAuditLogResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages
) {
}
