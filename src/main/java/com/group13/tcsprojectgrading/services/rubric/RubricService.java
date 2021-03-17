package com.group13.tcsprojectgrading.services.rubric;

import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.repositories.rubric.RubricRepository;
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

//    public Rubric getRubricByProjectId(String projectId) {
//        return repository.getById(projectId);
//    }

    public Rubric getRubricById(String id) {
        return repository.getById(id);
    }

    public Rubric addNewRubric(Rubric rubric) {
        return repository.save(rubric);
    }

    public List<Rubric> getAllRubrics() {
        return repository.findAll();
    }

    public void deleteRubric(String projectId) {
//        repository.deleteByProjectId(projectId);
        repository.deleteById(projectId);
    }
}
