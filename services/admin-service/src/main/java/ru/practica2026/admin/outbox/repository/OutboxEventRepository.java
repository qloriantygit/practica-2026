package ru.practica2026.admin.outbox.repository;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practica2026.admin.outbox.entity.OutboxEvent;
import ru.practica2026.admin.outbox.entity.OutboxStatus;

import java.time.Instant;
import java.util.List;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT e
            FROM OutboxEvent e
            WHERE e.status IN :statuses
              AND e.nextAttemptAt <= :now
            ORDER BY e.createdAt
            """)
    List<OutboxEvent> findReady(
            @Param("statuses")
            List<OutboxStatus> statuses,

            @Param("now")
            Instant now,

            Pageable pageable
    );
}
