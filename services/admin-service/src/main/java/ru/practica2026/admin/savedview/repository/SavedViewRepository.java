package ru.practica2026.admin.savedview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practica2026.admin.savedview.entity.SavedView;
import ru.practica2026.admin.user.entity.UserAccount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavedViewRepository
        extends JpaRepository<SavedView, Long> {

    Optional<SavedView> findByBusinessKeyAndOwner(
            UUID businessKey,
            UserAccount owner
    );

    List<SavedView> findAllByOwnerOrderByResourceTypeAscNameAsc(
            UserAccount owner
    );

    List<SavedView>
    findAllByOwnerAndResourceTypeIgnoreCaseOrderByNameAsc(
            UserAccount owner,
            String resourceType
    );

    boolean existsByOwnerAndResourceTypeIgnoreCaseAndNameIgnoreCase(
            UserAccount owner,
            String resourceType,
            String name
    );

    boolean existsByOwnerAndResourceTypeIgnoreCaseAndNameIgnoreCaseAndBusinessKeyNot(
            UserAccount owner,
            String resourceType,
            String name,
            UUID businessKey
    );
}
