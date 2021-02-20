package com.group13.tcsprojectgrading.service.project;

import com.group13.tcsprojectgrading.model.project.GradingVersion;
import com.group13.tcsprojectgrading.model.project.rubric.RubricVersion;
import com.group13.tcsprojectgrading.repository.project.GradingVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradingVersionService {

    private GradingVersionRepository repository;

    @Autowired
    private GradingService gradingService;

    @Autowired
    public GradingVersionService(GradingVersionRepository repository) {
        this.repository = repository;
    }

    public List<GradingVersion> getGradingVersions(GradingVersion gradingVersion) {
        return repository.findAll();
    }

    public void addNewGradingVersion(GradingVersion gradingVersion) {
        repository.save(gradingVersion);
        gradingService.setCurrentGradingVersion(gradingVersion.getGrading(), gradingVersion);
    }

}
