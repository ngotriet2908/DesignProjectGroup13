package com.group13.tcsprojectgrading.service.project.rubric;

import com.group13.tcsprojectgrading.model.project.rubric.BlockVersion;
import com.group13.tcsprojectgrading.repository.project.rubric.BlockVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockVersionService {
    private BlockVersionRepository repository;

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
