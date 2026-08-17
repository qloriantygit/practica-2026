package ru.practica2026.admin.sla.dto.response;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record WorkCalendarResponse(

        UUID businessKey,

        String code,

        String name,

        String description,

        String timezone,

        Set<DayOfWeek> workingDays,

        LocalTime workdayStart,

        LocalTime workdayEnd,

        boolean active,

        List<CalendarExceptionResponse> exceptions,

        Long version,

        Instant createdAt,

        Instant updatedAt,

        String createdBy,

        String updatedBy
) {
}
