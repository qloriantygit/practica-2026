package ru.practica2026.admin.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ru.practica2026.admin.organization.entity.Organization;
import ru.practica2026.admin.organization.entity.OrganizationStatus;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository
        extends JpaRepository<Organization, Long>,
        JpaSpecificationExecutor<Organization> {

    Optional<Organization> findByBusinessKey(UUID businessKey);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndBusinessKeyNot(
            String code,
            UUID businessKey
    );

    boolean existsByParent_IdAndStatus(
            Long parentId,
            OrganizationStatus status
    );
}
