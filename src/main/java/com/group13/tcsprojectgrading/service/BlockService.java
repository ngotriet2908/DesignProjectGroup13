package com.group13.tcsprojectgrading.service;

import com.group13.tcsprojectgrading.model.project.rubric.Block;
import com.group13.tcsprojectgrading.model.project.rubric.BlockVersion;
import com.group13.tcsprojectgrading.model.project.rubric.Rubric;
import com.group13.tcsprojectgrading.model.project.rubric.RubricVersion;
import com.group13.tcsprojectgrading.repository.project.rubric.BlockRepository;
import com.group13.tcsprojectgrading.repository.project.rubric.RubricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockService {
    private BlockRepository repository;

    @Autowired
    public BlockService(BlockRepository repository) {
        this.repository = repository;
    }

    public List<Block> getBlocks() {
        return repository.findAll();
    }

    public void addNewBlock(Block block) {
        repository.save(block);
    }

    public void setCurrentRubricVersion(Block block, BlockVersion blockVersion) {
        block.setCurrent_block_version_id(blockVersion.getId());
        repository.save(block);
    }
}
