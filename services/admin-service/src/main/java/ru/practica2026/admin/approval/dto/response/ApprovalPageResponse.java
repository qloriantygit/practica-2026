package ru.practica2026.admin.approval.dto.response;

import java.util.List;

public record ApprovalPageResponse(

        List<ApprovalResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages
) {
}
