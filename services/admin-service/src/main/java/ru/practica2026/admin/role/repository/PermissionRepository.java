package ru.practica2026.admin.role.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practica2026.admin.role.entity.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository
        extends JpaRepository<Permission, Long> {

    Optional<Permission> findByBusinessKey(UUID businessKey);

    List<Permission> findAllByOrderByCodeAsc();
}
