package com.group13.tcsprojectgrading.models.project.rubric;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Criterion {
    @Id
    @SequenceGenerator(
            name = "criterion_sequence",
            sequenceName = "criterion_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "criterion_sequence"
    )
    private Long id;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Block.class)
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne
    @JoinColumn(name="block_id")
    private Block block;

    private Long current_criterion_version_id;

    @OneToMany(mappedBy = "criterion")
    private Set<CriterionVersion> criterionVersions;


    public Criterion(Block block, Long current_criterion_version_id, Set<CriterionVersion> criterionVersions) {
        this.block = block;
        this.current_criterion_version_id = current_criterion_version_id;
        this.criterionVersions = criterionVersions;
    }

    public Criterion() {
    }

    public Criterion(Block block) {
        this.block = block;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Long getCurrent_criterion_version_id() {
        return current_criterion_version_id;
    }

    public void setCurrent_criterion_version_id(Long current_criterion_version_id) {
        this.current_criterion_version_id = current_criterion_version_id;
    }

    public Set<CriterionVersion> getCriterionVersions() {
        return criterionVersions;
    }

    public void setCriterionVersions(Set<CriterionVersion> criterionVersions) {
        this.criterionVersions = criterionVersions;
    }

    @Override
    public String toString() {
        return "Criterion{" +
                "id=" + id +
                ", block=" + block +
                ", current_criterion_version_id='" + current_criterion_version_id + '\'' +
                ", criterionVersions=" + criterionVersions +
                '}';
    }
}
