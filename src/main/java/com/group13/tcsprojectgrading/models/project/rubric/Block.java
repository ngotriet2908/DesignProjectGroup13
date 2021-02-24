package com.group13.tcsprojectgrading.models.project.rubric;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Block {
    @Id
    @SequenceGenerator(
            name = "block_sequence",
            sequenceName = "block_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "block_sequence"
    )
    private Long id;

    private Integer position;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Rubric.class)
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne
    @JoinColumn(name="rubric_id")
    private Rubric rubric;

    private Long current_block_version_id;

    @OneToMany(mappedBy = "block")
    private Set<BlockVersion> blockVersions;

    @OneToMany(mappedBy = "block")
    private Set<Criterion> criteria;

    public Block(Integer position, Rubric rubric, Long current_block_version_id, Set<BlockVersion> blockVersions) {
        this.position = position;
        this.rubric = rubric;
        this.current_block_version_id = current_block_version_id;
        this.blockVersions = blockVersions;
    }

    public Block(Rubric rubric) {
        this.rubric = rubric;
    }

    public Block() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Rubric getRubric() {
        return rubric;
    }

    public void setRubric(Rubric rubric) {
        this.rubric = rubric;
    }

    public Long getCurrent_block_version_id() {
        return current_block_version_id;
    }

    public void setCurrent_block_version_id(Long current_block_version_id) {
        this.current_block_version_id = current_block_version_id;
    }

    public Set<BlockVersion> getBlockVersions() {
        return blockVersions;
    }

    public void setBlockVersions(Set<BlockVersion> blockVersions) {
        this.blockVersions = blockVersions;
    }

    public Set<Criterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(Set<Criterion> criteria) {
        this.criteria = criteria;
    }

    @Override
    public String toString() {
        return "Block{" +
                "id=" + id +
                ", position=" + position +
                ", rubric=" + rubric +
                ", current_block_version_id='" + current_block_version_id + '\'' +
                '}';
    }
}
