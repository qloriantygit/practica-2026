package ru.practica2026.admin.sla.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateCalendarExceptionRequest(

        @NotNull
        LocalDate date,

        @NotNull
        Boolean workingDay,

        LocalTime workdayStart,

        LocalTime workdayEnd,

        @Size(max = 4000)
        String description
) {
}
