package ru.practica2026.admin.role.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practica2026.admin.role.entity.Permission;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository
        extends JpaRepository<Permission, Long> {

    Optional<Permission> findByBusinessKey(UUID businessKey);

    List<Permission> findAllByOrderByCodeAsc();

    @Query(
            value = """
                    SELECT DISTINCT p.code
                    FROM user_roles ur
                    JOIN roles r
                      ON r.id = ur.role_id
                    JOIN role_permissions rp
                      ON rp.role_id = r.id
                    JOIN permissions p
                      ON p.id = rp.permission_id
                    WHERE ur.user_id = :userId
                      AND r.status = 'ACTIVE'
                      AND ur.valid_from <= :now
                      AND (
                            ur.valid_to IS NULL
                            OR ur.valid_to > :now
                          )
                    ORDER BY p.code
                    """,
            nativeQuery = true
    )
    List<String> findActivePermissionCodesForUser(
            @Param("userId") Long userId,
            @Param("now") Instant now
    );
}
