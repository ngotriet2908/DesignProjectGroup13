package com.group13.tcsprojectgrading.service;

import com.group13.tcsprojectgrading.model.project.rubric.Rubric;
import com.group13.tcsprojectgrading.model.project.rubric.RubricVersion;
import com.group13.tcsprojectgrading.repository.project.rubric.RubricRepository;
import com.group13.tcsprojectgrading.repository.project.rubric.RubricVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RubricVersionService {
    private RubricVersionRepository repository;

    @Autowired
    private RubricService rubricService;

    @Autowired
    public RubricVersionService(RubricVersionRepository repository) {
        this.repository = repository;
    }

    public List<RubricVersion> getRubricsVersionByRubricId(Rubric rubric) {
        return repository.findRubricVersionByRubric(rubric);
    }

    public void addNewRubricVersion(RubricVersion rubricVersion) {
        repository.save(rubricVersion);
        rubricService.setCurrentRubricVersion(rubricVersion.getRubric(), rubricVersion);
    }
}
