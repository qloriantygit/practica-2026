package ru.practica2026.admin.sla.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practica2026.admin.sla.entity.WorkCalendar;

import java.util.Optional;
import java.util.UUID;

public interface WorkCalendarRepository
        extends JpaRepository<WorkCalendar, Long> {

    Optional<WorkCalendar> findByBusinessKey(
            UUID businessKey
    );

    boolean existsByCodeIgnoreCase(
            String code
    );

    @Query("""
            SELECT c
            FROM WorkCalendar c
            WHERE (
                :search IS NULL
                OR LOWER(c.code) LIKE :search
                OR LOWER(c.name) LIKE :search
                OR LOWER(c.description) LIKE :search
            )
            AND (
                :active IS NULL
                OR c.active = :active
            )
            """)
    Page<WorkCalendar> search(
            @Param("search")
            String search,

            @Param("active")
            Boolean active,

            Pageable pageable
    );
}
