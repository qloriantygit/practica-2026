package ru.practica2026.admin.audit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practica2026.admin.audit.entity.AdminAuditLog;

public interface AdminAuditLogRepository
        extends JpaRepository<AdminAuditLog, Long> {

    @Query("""
            SELECT a
            FROM AdminAuditLog a
            WHERE (
                :search IS NULL
                OR LOWER(a.actor) LIKE :search
                OR LOWER(a.action) LIKE :search
                OR LOWER(a.entityType) LIKE :search
                OR LOWER(a.correlationId) LIKE :search
            )
            AND (
                :success IS NULL
                OR a.success = :success
            )
            """)
    Page<AdminAuditLog> search(
            @Param("search")
            String search,

            @Param("success")
            Boolean success,

            Pageable pageable
    );
}
