package ru.practica2026.admin.expert.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practica2026.admin.expert.entity.ExpertProfile;
import ru.practica2026.admin.expert.entity.ExpertRestriction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpertRestrictionRepository
        extends JpaRepository<ExpertRestriction, Long> {

    Optional<ExpertRestriction> findByBusinessKey(
            UUID businessKey
    );

    List<ExpertRestriction>
    findAllByExpertProfileOrderByCodeAsc(
            ExpertProfile expertProfile
    );

    boolean existsByExpertProfileAndCodeIgnoreCase(
            ExpertProfile expertProfile,
            String code
    );

    boolean existsByExpertProfileAndCodeIgnoreCaseAndBusinessKeyNot(
            ExpertProfile expertProfile,
            String code,
            UUID businessKey
    );
}
