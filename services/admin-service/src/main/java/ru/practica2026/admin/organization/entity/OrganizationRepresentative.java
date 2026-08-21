package ru.practica2026.admin.organization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import ru.practica2026.admin.common.entity.BaseEntity;

@Entity
@Table(name = "organization_representatives")
public class OrganizationRepresentative extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "organization_id",
            nullable = false
    )
    private Organization organization;

    @Column(
            name = "first_name",
            nullable = false,
            length = 100
    )
    private String firstName;

    @Column(
            name = "last_name",
            nullable = false,
            length = 100
    )
    private String lastName;

    @Column(
            name = "middle_name",
            length = 100
    )
    private String middleName;

    @Column(
            name = "position",
            nullable = false,
            length = 200
    )
    private String position;

    @Column(
            name = "email",
            nullable = false,
            length = 255
    )
    private String email;

    @Column(
            name = "phone",
            length = 50
    )
    private String phone;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = true;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(
            Organization organization
    ) {
        this.organization = organization;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(
            String firstName
    ) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(
            String lastName
    ) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(
            String middleName
    ) {
        this.middleName = middleName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(
            String position
    ) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(
            String phone
    ) {
        this.phone = phone;
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
