package com.group13.tcsprojectgrading.service.project.rubric;

import com.group13.tcsprojectgrading.model.project.CourseGroup;
import com.group13.tcsprojectgrading.model.project.Project;
import com.group13.tcsprojectgrading.model.project.Submission;
import com.group13.tcsprojectgrading.model.project.rubric.Rubric;
import com.group13.tcsprojectgrading.model.project.rubric.RubricVersion;
import com.group13.tcsprojectgrading.repository.project.SubmissionRepository;
import com.group13.tcsprojectgrading.repository.project.rubric.RubricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class RubricService {

    private RubricRepository repository;

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
