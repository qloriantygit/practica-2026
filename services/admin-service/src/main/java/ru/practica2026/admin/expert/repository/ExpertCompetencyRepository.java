package ru.practica2026.admin.expert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practica2026.admin.directory.entity.DirectoryItem;
import ru.practica2026.admin.expert.entity.ExpertCompetency;
import ru.practica2026.admin.expert.entity.ExpertProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpertCompetencyRepository
        extends JpaRepository<ExpertCompetency, Long> {

    Optional<ExpertCompetency> findByBusinessKey(
            UUID businessKey
    );

    boolean existsByProfileAndSourceItem(
            ExpertProfile profile,
            DirectoryItem sourceItem
    );

    @Query("""
            SELECT ec
            FROM ExpertCompetency ec
            JOIN FETCH ec.sourceItem item
            JOIN FETCH item.directoryVersion version
            WHERE ec.profile = :profile
            ORDER BY item.code
            """)
    List<ExpertCompetency> findAllForProfile(
            @Param("profile")
            ExpertProfile profile
    );
}
