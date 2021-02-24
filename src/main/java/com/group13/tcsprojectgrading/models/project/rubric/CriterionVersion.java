package com.group13.tcsprojectgrading.models.project.rubric;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.group13.tcsprojectgrading.models.project.Grading;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
public class CriterionVersion {
    @Id
    @SequenceGenerator(
            name = "criterion_version_sequence",
            sequenceName = "criterion_version_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "criterion_version_sequence"
    )
    private Long id;

    private String name;

    private Timestamp created_date;

    private String created_by; //TODO: make this into relation with Teacher ?

    private Long previous_criterion_version_id;

    @OneToMany(mappedBy = "criterionVersion")
    private Set<Grading> gradings;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Criterion.class)
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne
    @JoinColumn(name="criterion_id")
    private Criterion criterion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Timestamp created_date) {
        this.created_date = created_date;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Long getPrevious_criterion_version_id() {
        return previous_criterion_version_id;
    }

    public void setPrevious_criterion_version_id(Long previous_criterion_version_id) {
        this.previous_criterion_version_id = previous_criterion_version_id;
    }

    public CriterionVersion(String name, Timestamp created_date, String created_by, Long previous_criterion_version_id, Criterion criterion) {
        this.name = name;
        this.created_date = created_date;
        this.created_by = created_by;
        this.previous_criterion_version_id = previous_criterion_version_id;
        this.criterion = criterion;
    }

    public Set<Grading> getGradings() {
        return gradings;
    }

    public void setGradings(Set<Grading> gradings) {
        this.gradings = gradings;
    }

    public CriterionVersion() {
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }
}
