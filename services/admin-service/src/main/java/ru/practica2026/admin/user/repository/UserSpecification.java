package ru.practica2026.admin.user.repository;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import ru.practica2026.admin.user.entity.UserAccount;
import ru.practica2026.admin.user.entity.UserStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class UserSpecification {

    private UserSpecification() {
    }

    public static Specification<UserAccount> withFilters(
            String search,
            UserStatus status,
            UUID organizationBusinessKey
    ) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates =
                    new ArrayList<>();

            if (search != null && !search.isBlank()) {

                String pattern =
                        "%" +
                        search.trim()
                                .toLowerCase(Locale.ROOT) +
                        "%";

                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.<String>get("username")
                                        ),
                                        pattern
                                ),
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.<String>get("email")
                                        ),
                                        pattern
                                ),
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.<String>get("firstName")
                                        ),
                                        pattern
                                ),
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.<String>get("lastName")
                                        ),
                                        pattern
                                )
                        )
                );
            }

            if (status != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("status"),
                                status
                        )
                );
            }

            if (organizationBusinessKey != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.join(
                                                "organization",
                                                JoinType.LEFT
                                        )
                                        .get("businessKey"),
                                organizationBusinessKey
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(
                            new Predicate[0]
                    )
            );
        };
    }
}
