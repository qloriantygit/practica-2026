package ru.practica2026.admin.sla.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CalendarExceptionResponse(

        UUID businessKey,

        LocalDate date,

        boolean workingDay,

        LocalTime workdayStart,

        LocalTime workdayEnd,

        String description,

        String createdBy
) {
}
