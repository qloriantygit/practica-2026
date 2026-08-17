package ru.practica2026.admin.directory.dto.response;

import java.util.List;

public record DirectoryPageResponse(

        List<DirectorySummaryResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages
) {
}
