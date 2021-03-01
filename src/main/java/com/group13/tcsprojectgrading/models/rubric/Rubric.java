package com.group13.tcsprojectgrading.models.rubric;

import org.springframework.data.annotation.Id;

import java.util.List;

public class Rubric {
    @Id
    public String id;
    private List<Block> blocks;

    public Rubric(String id, List<Block> blocks) {
        this.id = id;
        this.blocks = blocks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }
}
