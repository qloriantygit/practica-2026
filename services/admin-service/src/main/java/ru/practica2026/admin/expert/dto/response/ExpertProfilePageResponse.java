package ru.practica2026.admin.expert.dto.response;

import java.util.List;

public record ExpertProfilePageResponse(

        List<ExpertProfileResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages
) {
}
