package com.group13.tcsprojectgrading.service.project;

import com.group13.tcsprojectgrading.model.project.Grading;
import com.group13.tcsprojectgrading.model.project.GradingVersion;
import com.group13.tcsprojectgrading.repository.project.GradingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradingService {

    private GradingRepository repository;

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
