package ru.practica2026.admin.organization.repository;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import ru.practica2026.admin.organization.entity.Organization;
import ru.practica2026.admin.organization.entity.OrganizationStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class OrganizationSpecification {

    private OrganizationSpecification() {
    }

    public static Specification<Organization> withFilters(
            String search,
            OrganizationStatus status
    ) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {

                String pattern =
                        "%" +
                        search.trim().toLowerCase(Locale.ROOT) +
                        "%";

                Predicate codePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("code")),
                        pattern
                );

                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        pattern
                );

                predicates.add(
                        criteriaBuilder.or(
                                codePredicate,
                                namePredicate
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

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}
