package ru.practica2026.admin.role.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practica2026.admin.role.entity.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository
        extends JpaRepository<Role, Long> {

    Optional<Role> findByBusinessKey(UUID businessKey);

    boolean existsByCodeIgnoreCase(String code);

    List<Role> findAllByOrderByCodeAsc();
}
