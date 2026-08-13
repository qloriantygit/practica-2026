package ru.practica2026.admin.role.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "business_key",
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID businessKey;

    @Column(
            nullable = false,
            unique = true,
            length = 150
    )
    private String code;

    @Column(
            nullable = false,
            length = 255
    )
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public UUID getBusinessKey() {
        return businessKey;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
