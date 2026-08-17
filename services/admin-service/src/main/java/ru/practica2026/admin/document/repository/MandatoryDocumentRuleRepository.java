package ru.practica2026.admin.document.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practica2026.admin.document.entity.DocumentType;
import ru.practica2026.admin.document.entity.MandatoryDocumentRule;

import java.util.Optional;
import java.util.UUID;

public interface MandatoryDocumentRuleRepository
        extends JpaRepository<
                MandatoryDocumentRule,
                Long
                > {

    Optional<MandatoryDocumentRule>
    findByBusinessKey(
            UUID businessKey
    );

    boolean existsByContextCodeIgnoreCaseAndDocumentType(
            String contextCode,
            DocumentType documentType
    );

    @Query("""
            SELECT r
            FROM MandatoryDocumentRule r
            JOIN FETCH r.documentType d
            WHERE (
                :contextCode IS NULL
                OR LOWER(r.contextCode) = :contextCode
            )
            AND (
                :mandatory IS NULL
                OR r.mandatory = :mandatory
            )
            AND (
                :active IS NULL
                OR r.active = :active
            )
            """)
    Page<MandatoryDocumentRule> search(
            @Param("contextCode")
            String contextCode,

            @Param("mandatory")
            Boolean mandatory,

            @Param("active")
            Boolean active,

            Pageable pageable
    );
}
