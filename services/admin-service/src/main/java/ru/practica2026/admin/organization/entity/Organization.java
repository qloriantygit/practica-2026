package ru.practica2026.admin.organization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import ru.practica2026.admin.common.entity.BaseEntity;

@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {

    @Column(
            nullable = false,
            length = 100
    )
    private String code;

    @Column(
            nullable = false,
            length = 255
    )
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Organization parent;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 32
    )
    private OrganizationStatus status = OrganizationStatus.ACTIVE;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Organization getParent() {
        return parent;
    }

    public void setParent(Organization parent) {
        this.parent = parent;
    }

    public OrganizationStatus getStatus() {
        return status;
    }

    public void setStatus(OrganizationStatus status) {
        this.status = status;
    }
}
