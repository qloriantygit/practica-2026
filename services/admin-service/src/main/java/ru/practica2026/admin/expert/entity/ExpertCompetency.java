package ru.practica2026.admin.expert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import ru.practica2026.admin.common.entity.BaseEntity;
import ru.practica2026.admin.directory.entity.DirectoryItem;

@Entity
@Table(name = "expert_competencies")
public class ExpertCompetency extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "expert_profile_id",
            nullable = false
    )
    private ExpertProfile profile;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "directory_item_id",
            nullable = false
    )
    private DirectoryItem sourceItem;

    @Column(name = "proficiency_level")
    private Integer proficiencyLevel;

    @Column(name = "note")
    private String note;

    public ExpertProfile getProfile() {
        return profile;
    }

    public void setProfile(
            ExpertProfile profile
    ) {
        this.profile = profile;
    }

    public DirectoryItem getSourceItem() {
        return sourceItem;
    }

    public void setSourceItem(
            DirectoryItem sourceItem
    ) {
        this.sourceItem = sourceItem;
    }

    public Integer getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(
            Integer proficiencyLevel
    ) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public String getNote() {
        return note;
    }

    public void setNote(
            String note
    ) {
        this.note = note;
    }
}
