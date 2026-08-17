package ru.practica2026.admin.template.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practica2026.admin.template.entity.AdminTemplate;
import ru.practica2026.admin.template.entity.TemplateType;

import java.util.Optional;
import java.util.UUID;

public interface AdminTemplateRepository
        extends JpaRepository<AdminTemplate, Long> {

    Optional<AdminTemplate> findByBusinessKey(
            UUID businessKey
    );

    boolean existsByCodeIgnoreCase(
            String code
    );

    @Query("""
            SELECT t
            FROM AdminTemplate t
            WHERE (
                :search IS NULL
                OR LOWER(t.code) LIKE :search
                OR LOWER(t.name) LIKE :search
                OR LOWER(t.description) LIKE :search
            )
            AND (
                :templateType IS NULL
                OR t.templateType = :templateType
            )
            AND (
                :active IS NULL
                OR t.active = :active
            )
            """)
    Page<AdminTemplate> search(
            @Param("search")
            String search,

            @Param("templateType")
            TemplateType templateType,

            @Param("active")
            Boolean active,

            Pageable pageable
    );
}
