package com.group13.tcsprojectgrading.models.project.rubric;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class RubricVersion {
    @Id
    @SequenceGenerator(
            name = "rubric_version_sequence",
            sequenceName = "rubric_version_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "rubric_version_sequence"
    )
    private Long id;

    private String name;

    private Timestamp created_date;

    private String created_by; //TODO: make this into relation with Teacher ?

    private Long previous_rubric_version_id;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Rubric.class)
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne
    @JoinColumn(name="rubric_id")
    private Rubric rubric;

    public RubricVersion(String name, Timestamp created_date, String created_by, Long previous_rubric_version_id, Rubric rubric) {
        this.name = name;
        this.created_date = created_date;
        this.created_by = created_by;
        this.previous_rubric_version_id = previous_rubric_version_id;
        this.rubric = rubric;
    }

    public RubricVersion() {
    }

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

    public Long getPrevious_rubric_version_id() {
        return previous_rubric_version_id;
    }

    public void setPrevious_rubric_version_id(Long previous_rubric_version_id) {
        this.previous_rubric_version_id = previous_rubric_version_id;
    }

    public Rubric getRubric() {
        return rubric;
    }

    public void setRubric(Rubric rubric) {
        this.rubric = rubric;
    }

    @Override
    public String toString() {
        return "RubricVersion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", created_date=" + created_date +
                ", created_by='" + created_by + '\'' +
                ", previous_rubric_version_id='" + previous_rubric_version_id + '\'' +
                ", rubric=" + rubric +
                '}';
    }
}
