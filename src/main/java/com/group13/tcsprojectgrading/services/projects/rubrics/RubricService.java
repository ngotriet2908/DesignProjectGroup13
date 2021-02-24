package com.group13.tcsprojectgrading.services.projects.rubrics;

import com.group13.tcsprojectgrading.models.project.rubric.Rubric;
import com.group13.tcsprojectgrading.models.project.rubric.RubricVersion;
import com.group13.tcsprojectgrading.repositories.project.rubric.RubricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RubricService {
    private final RubricRepository repository;

    @Autowired
    public RubricService(RubricRepository repository) {
        this.repository = repository;
    }

    public List<Rubric> getRubrics() {
        return repository.findAll();
    }

    public void addNewRubrics(Rubric rubric) {
        repository.save(rubric);
    }

    public void setCurrentRubricVersion(Rubric rubric, RubricVersion rubricVersion) {
        rubric.setCurrent_rubric_version_id(rubricVersion.getId());
        repository.save(rubric);
    }
}
