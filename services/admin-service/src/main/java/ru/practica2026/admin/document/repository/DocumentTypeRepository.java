package ru.practica2026.admin.document.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practica2026.admin.document.entity.DocumentType;

import java.util.Optional;
import java.util.UUID;

public interface DocumentTypeRepository
        extends JpaRepository<DocumentType, Long> {

    Optional<DocumentType> findByBusinessKey(
            UUID businessKey
    );

    boolean existsByCodeIgnoreCase(
            String code
    );

    @Query("""
            SELECT d
            FROM DocumentType d
            WHERE (
                :search IS NULL
                OR LOWER(d.code) LIKE :search
                OR LOWER(d.name) LIKE :search
                OR LOWER(d.description) LIKE :search
            )
            AND (
                :active IS NULL
                OR d.active = :active
            )
            """)
    Page<DocumentType> search(
            @Param("search")
            String search,

            @Param("active")
            Boolean active,

            Pageable pageable
    );
}
