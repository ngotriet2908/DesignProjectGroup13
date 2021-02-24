package com.group13.tcsprojectgrading.services.projects.rubrics;

import com.group13.tcsprojectgrading.models.project.rubric.BlockVersion;
import com.group13.tcsprojectgrading.repositories.project.rubric.BlockVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockVersionService {
    private final BlockVersionRepository repository;

    @Autowired
    private BlockService blockService;

    @Autowired
    public BlockVersionService(BlockVersionRepository repository) {
        this.repository = repository;
    }

    public void addNewBlockVersion(BlockVersion blockVersion) {
        repository.save(blockVersion);
        blockService.setCurrentRubricVersion(blockVersion.getBlock(), blockVersion);
    }
}
