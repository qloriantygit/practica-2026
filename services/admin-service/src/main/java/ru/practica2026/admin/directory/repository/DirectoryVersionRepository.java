package ru.practica2026.admin.directory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practica2026.admin.directory.entity.Directory;
import ru.practica2026.admin.directory.entity.DirectoryVersion;
import ru.practica2026.admin.directory.entity.DirectoryVersionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DirectoryVersionRepository
        extends JpaRepository<DirectoryVersion, Long> {

    Optional<DirectoryVersion> findByBusinessKey(
            UUID businessKey
    );

    List<DirectoryVersion>
    findAllByDirectoryOrderByVersionNumberDesc(
            Directory directory
    );

    Optional<DirectoryVersion>
    findTopByDirectoryOrderByVersionNumberDesc(
            Directory directory
    );

    Optional<DirectoryVersion>
    findByDirectoryAndVersionNumber(
            Directory directory,
            Integer versionNumber
    );

    boolean existsByDirectoryAndStatus(
            Directory directory,
            DirectoryVersionStatus status
    );
}
