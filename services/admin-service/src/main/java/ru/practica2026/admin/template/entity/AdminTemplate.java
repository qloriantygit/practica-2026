package ru.practica2026.admin.template.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import ru.practica2026.admin.common.entity.BaseEntity;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "admin_templates")
public class AdminTemplate extends BaseEntity {

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

    @Enumerated(EnumType.STRING)
    @Column(
            name = "template_type",
            nullable = false,
            length = 32
    )
    private TemplateType templateType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "channel",
            length = 32
    )
    private TemplateChannel channel;

    @Column(
            name = "subject",
            length = 500
    )
    private String subject;

    @Column(
            name = "body",
            nullable = false
    )
    private String body;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "variables",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private Set<String> variables =
            new LinkedHashSet<>();

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

    public void setDescription(String description) {
        this.description = description;
    }

    public TemplateType getTemplateType() {
        return templateType;
    }

    public void setTemplateType(
            TemplateType templateType
    ) {
        this.templateType = templateType;
    }

    public TemplateChannel getChannel() {
        return channel;
    }

    public void setChannel(
            TemplateChannel channel
    ) {
        this.channel = channel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Set<String> getVariables() {
        return variables;
    }

    public void setVariables(
            Set<String> variables
    ) {
        this.variables =
                variables == null
                        ? new LinkedHashSet<>()
                        : new LinkedHashSet<>(variables);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
