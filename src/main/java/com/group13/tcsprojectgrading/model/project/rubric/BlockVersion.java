package com.group13.tcsprojectgrading.model.project.rubric;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.group13.tcsprojectgrading.model.project.Project;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class BlockVersion {
    @Id
    @SequenceGenerator(
            name = "block_version_sequence",
            sequenceName = "block_version_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "block_version_sequence"
    )
    private Long id;

    private String name;

    private Timestamp created_date;

    private String created_by; //TODO: make this into relation with Teacher ?

    private Long previous_block_version_id;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Block.class)
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne
    @JoinColumn(name="block_id")
    private Block block;

    public BlockVersion(String name, Timestamp created_date, String created_by, Long previous_block_version_id, Block block) {
        this.name = name;
        this.created_date = created_date;
        this.created_by = created_by;
        this.previous_block_version_id = previous_block_version_id;
        this.block = block;
    }

    public BlockVersion() {
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

    public Long getPrevious_block_version_id() {
        return previous_block_version_id;
    }

    public void setPrevious_block_version_id(Long previous_block_version_id) {
        this.previous_block_version_id = previous_block_version_id;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    @Override
    public String toString() {
        return "BlockVersion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", created_date=" + created_date +
                ", created_by='" + created_by + '\'' +
                ", previous_block_version_id='" + previous_block_version_id + '\'' +
                ", block=" + block +
                '}';
    }
}
