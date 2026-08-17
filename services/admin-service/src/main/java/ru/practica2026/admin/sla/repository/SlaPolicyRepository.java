package ru.practica2026.admin.sla.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practica2026.admin.sla.entity.SlaPolicy;

import java.util.Optional;
import java.util.UUID;

public interface SlaPolicyRepository
        extends JpaRepository<SlaPolicy, Long> {

    Optional<SlaPolicy> findByBusinessKey(
            UUID businessKey
    );

    boolean existsByCodeIgnoreCase(
            String code
    );

    @Query("""
            SELECT s
            FROM SlaPolicy s
            WHERE (
                :search IS NULL
                OR LOWER(s.code) LIKE :search
                OR LOWER(s.name) LIKE :search
                OR LOWER(s.description) LIKE :search
            )
            AND (
                :active IS NULL
                OR s.active = :active
            )
            """)
    Page<SlaPolicy> search(
            @Param("search")
            String search,

            @Param("active")
            Boolean active,

            Pageable pageable
    );
}
