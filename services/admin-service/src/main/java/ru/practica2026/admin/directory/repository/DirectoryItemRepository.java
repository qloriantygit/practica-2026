package ru.practica2026.admin.directory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practica2026.admin.directory.entity.DirectoryItem;
import ru.practica2026.admin.directory.entity.DirectoryVersion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DirectoryItemRepository
        extends JpaRepository<DirectoryItem, Long> {

    Optional<DirectoryItem> findByBusinessKey(
            UUID businessKey
    );

    List<DirectoryItem>
    findAllByDirectoryVersionOrderBySortOrderAscCodeAsc(
            DirectoryVersion directoryVersion
    );

    boolean existsByDirectoryVersionAndCodeIgnoreCase(
            DirectoryVersion directoryVersion,
            String code
    );

    boolean existsByDirectoryVersionAndCodeIgnoreCaseAndBusinessKeyNot(
            DirectoryVersion directoryVersion,
            String code,
            UUID businessKey
    );

    long countByDirectoryVersion(
            DirectoryVersion directoryVersion
    );

    @Query("""
            SELECT i
            FROM DirectoryItem i
            WHERE i.directoryVersion = :directoryVersion
              AND (
                    :search IS NULL
                    OR LOWER(i.code) LIKE :search
                    OR LOWER(i.name) LIKE :search
                    OR LOWER(i.description) LIKE :search
                  )
              AND (
                    :enabled IS NULL
                    OR i.enabled = :enabled
                  )
            """)
    Page<DirectoryItem> search(
            @Param("directoryVersion")
            DirectoryVersion directoryVersion,

            @Param("search")
            String search,

            @Param("enabled")
            Boolean enabled,

            Pageable pageable
    );
}
