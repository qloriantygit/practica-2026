package ru.practica2026.admin.sla.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import ru.practica2026.admin.common.entity.BaseEntity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "work_calendars")
public class WorkCalendar extends BaseEntity {

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
            name = "timezone",
            nullable = false,
            length = 100
    )
    private String timezone;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "working_days",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private Set<DayOfWeek> workingDays =
            new LinkedHashSet<>();

    @JdbcTypeCode(SqlTypes.LOCAL_TIME)
    @Column(
            name = "workday_start",
            nullable = false
    )
    private LocalTime workdayStart;

    @JdbcTypeCode(SqlTypes.LOCAL_TIME)
    @Column(
            name = "workday_end",
            nullable = false
    )
    private LocalTime workdayEnd;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = true;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(
            String timezone
    ) {
        this.timezone = timezone;
    }

    public Set<DayOfWeek> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(
            Set<DayOfWeek> workingDays
    ) {
        this.workingDays =
                workingDays == null
                        ? new LinkedHashSet<>()
                        : new LinkedHashSet<>(
                                workingDays
                        );
    }

    public LocalTime getWorkdayStart() {
        return workdayStart;
    }

    public void setWorkdayStart(
            LocalTime workdayStart
    ) {
        this.workdayStart = workdayStart;
    }

    public LocalTime getWorkdayEnd() {
        return workdayEnd;
    }

    public void setWorkdayEnd(
            LocalTime workdayEnd
    ) {
        this.workdayEnd = workdayEnd;
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
