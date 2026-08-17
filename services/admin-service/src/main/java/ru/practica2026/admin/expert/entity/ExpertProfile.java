package ru.practica2026.admin.expert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import ru.practica2026.admin.common.entity.BaseEntity;
import ru.practica2026.admin.user.entity.UserAccount;

@Entity
@Table(name = "expert_profiles")
public class ExpertProfile extends BaseEntity {

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private UserAccount user;

    @Column(
            name = "specialization",
            nullable = false,
            length = 255
    )
    private String specialization;

    @Column(name = "bio")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 32
    )
    private ExpertProfileStatus status;

    @Column(
            name = "available",
            nullable = false
    )
    private boolean available = true;

    public UserAccount getUser() {
        return user;
    }

    public void setUser(
            UserAccount user
    ) {
        this.user = user;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(
            String specialization
    ) {
        this.specialization = specialization;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(
            String bio
    ) {
        this.bio = bio;
    }

    public ExpertProfileStatus getStatus() {
        return status;
    }

    public void setStatus(
            ExpertProfileStatus status
    ) {
        this.status = status;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(
            boolean available
    ) {
        this.available = available;
    }
}
