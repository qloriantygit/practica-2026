package ru.practica2026.admin.directory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import ru.practica2026.admin.common.entity.BaseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "directory_items")
public class DirectoryItem extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "directory_version_id",
            nullable = false
    )
    private DirectoryVersion directoryVersion;

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
            name = "enabled",
            nullable = false
    )
    private boolean enabled = true;

    @Column(
            name = "sort_order",
            nullable = false
    )
    private Integer sortOrder = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "attributes",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private Map<String, Object> attributes =
            new LinkedHashMap<>();

    public DirectoryVersion getDirectoryVersion() {
        return directoryVersion;
    }

    public void setDirectoryVersion(
            DirectoryVersion directoryVersion
    ) {
        this.directoryVersion = directoryVersion;
    }

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(
            boolean enabled
    ) {
        this.enabled = enabled;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(
            Integer sortOrder
    ) {
        this.sortOrder = sortOrder;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(
            Map<String, Object> attributes
    ) {
        this.attributes =
                attributes == null
                        ? new LinkedHashMap<>()
                        : new LinkedHashMap<>(attributes);
    }
}
