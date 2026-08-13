package ru.practica2026.admin.role.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practica2026.admin.role.entity.RolePermission;
import ru.practica2026.admin.role.entity.RolePermissionId;

import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository
        extends JpaRepository<RolePermission, RolePermissionId> {

    Optional<RolePermission> findByRole_IdAndPermission_Id(
            Long roleId,
            Long permissionId
    );

    @Query("""
            select rp
            from RolePermission rp
            join fetch rp.permission p
            where rp.role.id = :roleId
            order by p.code
            """)
    List<RolePermission> findAllByRoleIdWithPermission(
            @Param("roleId")
            Long roleId
    );
}
