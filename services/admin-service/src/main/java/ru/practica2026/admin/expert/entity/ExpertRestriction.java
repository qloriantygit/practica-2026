package ru.practica2026.admin.expert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import ru.practica2026.admin.common.entity.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "expert_restrictions")
public class ExpertRestriction extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "expert_profile_id",
            nullable = false
    )
    private ExpertProfile expertProfile;

    @Column(
            name = "code",
            nullable = false,
            length = 100
    )
    private String code;

    @Column(
            name = "description",
            nullable = false,
            length = 1000
    )
    private String description;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = true;

    public ExpertProfile getExpertProfile() {
        return expertProfile;
    }

    public void setExpertProfile(
            ExpertProfile expertProfile
    ) {
        this.expertProfile = expertProfile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(
            String code
    ) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(
            LocalDate validFrom
    ) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(
            LocalDate validTo
    ) {
        this.validTo = validTo;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(
            boolean active
    ) {
        this.active = active;
    }
}
