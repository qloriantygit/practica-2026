package ru.practica2026.admin.sla.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record CreateWorkCalendarRequest(

        @NotBlank
        @Size(max = 100)
        String code,

        @NotBlank
        @Size(max = 255)
        String name,

        @Size(max = 4000)
        String description,

        @NotBlank
        @Size(max = 100)
        String timezone,

        @NotEmpty
        Set<DayOfWeek> workingDays,

        @NotNull
        LocalTime workdayStart,

        @NotNull
        LocalTime workdayEnd,

        Boolean active
) {
}
