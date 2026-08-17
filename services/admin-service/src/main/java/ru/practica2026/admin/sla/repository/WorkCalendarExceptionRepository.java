package ru.practica2026.admin.sla.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practica2026.admin.sla.entity.WorkCalendar;
import ru.practica2026.admin.sla.entity.WorkCalendarException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkCalendarExceptionRepository
        extends JpaRepository<
                WorkCalendarException,
                Long
                > {

    Optional<WorkCalendarException>
    findByBusinessKey(
            UUID businessKey
    );

    Optional<WorkCalendarException>
    findByCalendarAndExceptionDate(
            WorkCalendar calendar,
            LocalDate exceptionDate
    );

    boolean existsByCalendarAndExceptionDate(
            WorkCalendar calendar,
            LocalDate exceptionDate
    );

    List<WorkCalendarException>
    findAllByCalendarOrderByExceptionDateAsc(
            WorkCalendar calendar
    );
}
