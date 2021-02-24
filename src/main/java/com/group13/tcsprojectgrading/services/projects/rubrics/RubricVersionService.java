package com.group13.tcsprojectgrading.services.projects.rubrics;

import com.group13.tcsprojectgrading.models.project.rubric.Rubric;
import com.group13.tcsprojectgrading.models.project.rubric.RubricVersion;
import com.group13.tcsprojectgrading.repositories.project.rubric.RubricVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RubricVersionService {
    private final RubricVersionRepository repository;

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
