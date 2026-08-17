package ru.practica2026.admin.document.dto.response;

import java.util.List;

public record MandatoryDocumentRulePageResponse(

        List<MandatoryDocumentRuleResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages
) {
}
