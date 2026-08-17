package ru.practica2026.admin.directory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import ru.practica2026.admin.common.entity.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "directory_versions")
public class DirectoryVersion extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "directory_id",
            nullable = false
    )
    private Directory directory;

    @Column(
            name = "version_number",
            nullable = false
    )
    private Integer versionNumber;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 32
    )
    private DirectoryVersionStatus status;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(
            Directory directory
    ) {
        this.directory = directory;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(
            Integer versionNumber
    ) {
        this.versionNumber = versionNumber;
    }

    public DirectoryVersionStatus getStatus() {
        return status;
    }

    public void setStatus(
            DirectoryVersionStatus status
    ) {
        this.status = status;
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
}
