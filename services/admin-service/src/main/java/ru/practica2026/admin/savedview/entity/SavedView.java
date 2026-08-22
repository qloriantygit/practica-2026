package ru.practica2026.admin.savedview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import ru.practica2026.admin.common.entity.BaseEntity;
import ru.practica2026.admin.user.entity.UserAccount;

import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "saved_views")
public class SavedView extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "owner_user_id",
            nullable = false
    )
    private UserAccount owner;

    @Column(
            name = "name",
            nullable = false,
            length = 150
    )
    private String name;

    @Column(
            name = "resource_type",
            nullable = false,
            length = 100
    )
    private String resourceType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "filters",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private Map<String, Object> filters =
            new LinkedHashMap<>();

    @Column(
            name = "sort_by",
            length = 100
    )
    private String sortBy;

    @Column(
            name = "sort_direction",
            length = 4
    )
    private String sortDirection;

    public UserAccount getOwner() {
        return owner;
    }

    public void setOwner(
            UserAccount owner
    ) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name = name;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(
            String resourceType
    ) {
        this.resourceType = resourceType;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(
            Map<String, Object> filters
    ) {
        this.filters = filters;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(
            String sortBy
    ) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(
            String sortDirection
    ) {
        this.sortDirection = sortDirection;
    }
}
