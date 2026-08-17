package ru.practica2026.admin.directory.dto.request;

import java.time.LocalDate;

public record CreateDirectoryVersionRequest(

        LocalDate validFrom,

        LocalDate validTo
) {
}
