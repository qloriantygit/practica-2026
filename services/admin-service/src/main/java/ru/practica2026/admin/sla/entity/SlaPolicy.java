package ru.practica2026.admin.sla.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import ru.practica2026.admin.common.entity.BaseEntity;

@Entity
@Table(name = "sla_policies")
public class SlaPolicy extends BaseEntity {

    @Column(
            name = "code",
            nullable = false,
            length = 100
    )
    private String code;

    @Column(
            name = "name",
            nullable = false,
            length = 255
    )
    private String name;

    @Column(name = "description")
    private String description;

    @Column(
            name = "response_minutes",
            nullable = false
    )
    private Integer responseMinutes;

    @Column(
            name = "resolution_minutes",
            nullable = false
    )
    private Integer resolutionMinutes;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "calendar_id",
            nullable = false
    )
    private WorkCalendar calendar;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = true;

    public String getCode() {
        return code;
    }

    public void setCode(
            String code
    ) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }

    public Integer getResponseMinutes() {
        return responseMinutes;
    }

    public void setResponseMinutes(
            Integer responseMinutes
    ) {
        this.responseMinutes = responseMinutes;
    }

    public Integer getResolutionMinutes() {
        return resolutionMinutes;
    }

    public void setResolutionMinutes(
            Integer resolutionMinutes
    ) {
        this.resolutionMinutes =
                resolutionMinutes;
    }

    public WorkCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(
            WorkCalendar calendar
    ) {
        this.calendar = calendar;
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
