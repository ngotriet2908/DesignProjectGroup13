package com.group13.tcsprojectgrading.services.projects;

import com.group13.tcsprojectgrading.models.project.Grading;
import com.group13.tcsprojectgrading.models.project.GradingVersion;
import com.group13.tcsprojectgrading.repositories.project.GradingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradingService {
    private final GradingRepository repository;

    @Autowired
    public GradingService(GradingRepository repository) {
        this.repository = repository;
    }

    public List<Grading> getGradings() {
        return repository.findAll();
    }

    public void saveNewGrading(Grading grading) {
        repository.save(grading);
    }

    public void setCurrentGradingVersion(Grading grading, GradingVersion gradingVersion) {
        grading.setCurrentGradingVersion(gradingVersion.getId());
        repository.save(grading);
    }
}
