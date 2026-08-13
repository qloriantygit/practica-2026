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
}
