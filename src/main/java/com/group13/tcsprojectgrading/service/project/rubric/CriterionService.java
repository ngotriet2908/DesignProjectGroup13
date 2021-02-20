package com.group13.tcsprojectgrading.service.project.rubric;

import com.group13.tcsprojectgrading.model.project.rubric.Block;
import com.group13.tcsprojectgrading.model.project.rubric.BlockVersion;
import com.group13.tcsprojectgrading.model.project.rubric.Criterion;
import com.group13.tcsprojectgrading.model.project.rubric.CriterionVersion;
import com.group13.tcsprojectgrading.repository.project.rubric.BlockRepository;
import com.group13.tcsprojectgrading.repository.project.rubric.CriterionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CriterionService {

    private CriterionRepository repository;

    @Autowired
    public CriterionService(CriterionRepository repository) {
        this.repository = repository;
    }

    public List<Criterion> getCriterion() {
        return repository.findAll();
    }

    public void addNewCriterion(Criterion criterion) {
        repository.save(criterion);
    }

    public void setCurrentCriterionVersion(Criterion criterion, CriterionVersion criterionVersion) {
        criterion.setCurrent_criterion_version_id(criterionVersion.getId());
        repository.save(criterion);
    }
}
