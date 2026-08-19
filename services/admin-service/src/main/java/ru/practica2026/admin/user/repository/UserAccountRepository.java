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
}
