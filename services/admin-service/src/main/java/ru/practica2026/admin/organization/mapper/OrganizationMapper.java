package ru.practica2026.admin.organization.mapper;

import ru.practica2026.admin.organization.dto.response.OrganizationResponse;
import ru.practica2026.admin.organization.entity.Organization;

public final class OrganizationMapper {

    private OrganizationMapper() {
    }

    public static OrganizationResponse toResponse(
            Organization organization
    ) {
        return new OrganizationResponse(
                organization.getBusinessKey(),
                organization.getCode(),
                organization.getName(),

                organization.getParent() == null
                        ? null
                        : organization.getParent().getBusinessKey(),

                organization.getStatus(),
                organization.getVersion(),
                organization.getCreatedAt(),
                organization.getUpdatedAt(),
                organization.getCreatedBy(),
                organization.getUpdatedBy()
        );
    }
}
