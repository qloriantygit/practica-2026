package ru.practica2026.admin.sla.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import ru.practica2026.admin.common.entity.BaseEntity;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "work_calendar_exceptions")
public class WorkCalendarException extends BaseEntity {

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
            name = "exception_date",
            nullable = false
    )
    private LocalDate exceptionDate;

    @Column(
            name = "working_day",
            nullable = false
    )
    private boolean workingDay;

    @JdbcTypeCode(SqlTypes.LOCAL_TIME)
    @Column(name = "workday_start")
    private LocalTime workdayStart;

    @JdbcTypeCode(SqlTypes.LOCAL_TIME)
    @Column(name = "workday_end")
    private LocalTime workdayEnd;

    @Column(name = "description")
    private String description;

    public WorkCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(
            WorkCalendar calendar
    ) {
        this.calendar = calendar;
    }

    public LocalDate getExceptionDate() {
        return exceptionDate;
    }

    public void setExceptionDate(
            LocalDate exceptionDate
    ) {
        this.exceptionDate =
                exceptionDate;
    }

    public boolean isWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(
            boolean workingDay
    ) {
        this.workingDay = workingDay;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }
}
