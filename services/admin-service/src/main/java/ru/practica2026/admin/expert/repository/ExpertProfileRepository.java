package ru.practica2026.admin.expert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practica2026.admin.expert.entity.ExpertProfile;
import ru.practica2026.admin.expert.entity.ExpertProfileStatus;
import ru.practica2026.admin.user.entity.UserAccount;

import java.util.Optional;
import java.util.UUID;

public interface ExpertProfileRepository
        extends JpaRepository<ExpertProfile, Long> {

    Optional<ExpertProfile> findByBusinessKey(
            UUID businessKey
    );

    boolean existsByUser(
            UserAccount user
    );

    @Query("""
            SELECT ep
            FROM ExpertProfile ep
            JOIN ep.user u
            WHERE (
                :search IS NULL
                OR LOWER(u.username) LIKE :search
                OR LOWER(u.email) LIKE :search
                OR LOWER(ep.specialization) LIKE :search
                OR LOWER(ep.bio) LIKE :search
            )
            AND (
                :status IS NULL
                OR ep.status = :status
            )
            AND (
                :available IS NULL
                OR ep.available = :available
            )
            """)
    Page<ExpertProfile> search(
            @Param("search")
            String search,

            @Param("status")
            ExpertProfileStatus status,

            @Param("available")
            Boolean available,

            Pageable pageable
    );
}
