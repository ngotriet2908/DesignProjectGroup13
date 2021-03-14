package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.GraderId;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.repositories.GraderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GraderService {
    private GraderRepository repository;

    @Autowired
    public GraderService(GraderRepository repository) {
        this.repository = repository;
    }

    public Grader addNewGrader(Grader grader) {
        Grader grader1 = repository.findById(new GraderId(grader.getUserId(), grader.getProject().getProjectCompositeKey())).orElse(null);
        if (grader1 == null) {
            return repository.save(grader);
        }
        return grader1;
    }

    public List<Grader> getGraderFromProject(Project project) {
        return repository.findGraderByProject(project);
    }

    public void deleteGrader(Grader grader) {
        repository.delete(grader);
    }

    public Grader getGraderFromGraderId(String userId, Project project) {
        return repository.findById(new GraderId(userId, project.getProjectCompositeKey())).orElse(null);
    }
}
