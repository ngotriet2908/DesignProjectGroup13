package com.group13.tcsprojectgrading.service.project.rubric;

import com.group13.tcsprojectgrading.model.project.rubric.CriterionVersion;
import com.group13.tcsprojectgrading.repository.project.rubric.CriterionVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CriterionVersionService {
    private CriterionVersionRepository repository;

    @Autowired
    private CriterionService criterionService;

    @Autowired
    public CriterionVersionService(CriterionVersionRepository repository) {
        this.repository = repository;
    }

    public void addNewCriterionVersion(CriterionVersion criterionVersion) {
        repository.save(criterionVersion);
        criterionService.setCurrentCriterionVersion(criterionVersion.getCriterion(), criterionVersion);
    }
}
