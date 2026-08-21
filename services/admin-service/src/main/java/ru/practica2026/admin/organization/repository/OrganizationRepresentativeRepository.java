package ru.practica2026.admin.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practica2026.admin.organization.entity.Organization;
import ru.practica2026.admin.organization.entity.OrganizationRepresentative;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepresentativeRepository
        extends JpaRepository<OrganizationRepresentative, Long> {

    Optional<OrganizationRepresentative> findByBusinessKey(
            UUID businessKey
    );

    List<OrganizationRepresentative>
    findAllByOrganizationOrderByLastNameAscFirstNameAsc(
            Organization organization
    );

    boolean existsByOrganizationAndEmailIgnoreCase(
            Organization organization,
            String email
    );

    boolean existsByOrganizationAndEmailIgnoreCaseAndBusinessKeyNot(
            Organization organization,
            String email,
            UUID businessKey
    );
}
