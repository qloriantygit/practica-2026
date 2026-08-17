package ru.practica2026.admin.directory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practica2026.admin.directory.entity.Directory;
import ru.practica2026.admin.directory.entity.DirectoryVersionStatus;

import java.util.Optional;
import java.util.UUID;

public interface DirectoryRepository
        extends JpaRepository<Directory, Long> {

    Optional<Directory> findByBusinessKey(
            UUID businessKey
    );

    Optional<Directory> findByCodeIgnoreCase(
            String code
    );

    boolean existsByCodeIgnoreCase(
            String code
    );

    @Query("""
            SELECT d
            FROM Directory d
            WHERE (
                :search IS NULL
                OR LOWER(d.code) LIKE :search
                OR LOWER(d.name) LIKE :search
                OR LOWER(d.description) LIKE :search
            )
            AND (
                :status IS NULL
                OR EXISTS (
                    SELECT dv.id
                    FROM DirectoryVersion dv
                    WHERE dv.directory = d
                      AND dv.status = :status
                      AND dv.versionNumber = (
                          SELECT MAX(dv2.versionNumber)
                          FROM DirectoryVersion dv2
                          WHERE dv2.directory = d
                      )
                )
            )
            """)
    Page<Directory> search(
            @Param("search") String search,
            @Param("status") DirectoryVersionStatus status,
            Pageable pageable
    );
}
