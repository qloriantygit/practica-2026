package ru.practica2026.admin.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practica2026.admin.user.entity.UserRole;
import ru.practica2026.admin.user.entity.UserRoleId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserRoleRepository
        extends JpaRepository<UserRole, UserRoleId> {

    Optional<UserRole> findByUser_IdAndRole_Id(
            Long userId,
            Long roleId
    );

    @Query("""
            select ur
            from UserRole ur
            join fetch ur.role r
            where ur.user.id = :userId
            order by r.code
            """)
    List<UserRole> findAllByUserIdWithRole(
            @Param("userId")
            Long userId
    );

    @Query("""
            select count(ur)
            from UserRole ur
            where ur.role.id = :roleId
              and (
                    ur.validTo is null
                    or ur.validTo > :now
                  )
            """)
    long countNonExpiredAssignments(
            @Param("roleId")
            Long roleId,

            @Param("now")
            Instant now
    );
}
