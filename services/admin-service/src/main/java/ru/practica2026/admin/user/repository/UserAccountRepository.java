package ru.practica2026.admin.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ru.practica2026.admin.user.entity.UserAccount;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository
        extends JpaRepository<UserAccount, Long>,
        JpaSpecificationExecutor<UserAccount> {

    Optional<UserAccount> findByBusinessKey(UUID businessKey);

    Optional<UserAccount> findByExternalId(String externalId);

    Optional<UserAccount> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCaseAndBusinessKeyNot(
            String username,
            UUID businessKey
    );

    boolean existsByEmailIgnoreCaseAndBusinessKeyNot(
            String email,
            UUID businessKey
    );

    java.util.Optional<UserAccount> findFirstByUsernameIgnoreCase(
            String username
    );

    @org.springframework.data.jpa.repository.Query(
            value = """
                    SELECT EXISTS (
                        SELECT 1
                        FROM user_roles ur
                        JOIN roles r
                          ON r.id = ur.role_id
                        WHERE ur.user_id = :userId
                          AND r.code = 'ADMIN'
                          AND r.status = 'ACTIVE'
                          AND (
                              ur.valid_from IS NULL
                              OR ur.valid_from <= CURRENT_TIMESTAMP
                          )
                          AND (
                              ur.valid_to IS NULL
                              OR ur.valid_to > CURRENT_TIMESTAMP
                          )
                    )
                    """,
            nativeQuery = true
    )
    boolean hasActiveAdminRole(
            @org.springframework.data.repository.query.Param("userId")
            Long userId
    );
}
