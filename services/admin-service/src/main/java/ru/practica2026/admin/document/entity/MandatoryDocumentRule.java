package ru.practica2026.admin.document.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import ru.practica2026.admin.common.entity.BaseEntity;

@Entity
@Table(name = "mandatory_document_rules")
public class MandatoryDocumentRule
        extends BaseEntity {

    @Column(
            name = "context_code",
            nullable = false,
            length = 100
    )
    private String contextCode;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "document_type_id",
            nullable = false
    )
    private DocumentType documentType;

    @Column(
            name = "mandatory",
            nullable = false
    )
    private boolean mandatory = true;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = true;

    public String getContextCode() {
        return contextCode;
    }

    public void setContextCode(
            String contextCode
    ) {
        this.contextCode = contextCode;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(
            DocumentType documentType
    ) {
        this.documentType = documentType;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(
            boolean mandatory
    ) {
        this.mandatory = mandatory;
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
