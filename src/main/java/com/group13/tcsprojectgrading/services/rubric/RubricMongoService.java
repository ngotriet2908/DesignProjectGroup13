package com.group13.tcsprojectgrading.services.rubric;

import com.group13.tcsprojectgrading.models.project.Attachment;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.repositories.project.AttachmentRepository;
import com.group13.tcsprojectgrading.repositories.rubric.RubricMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RubricMongoService {
    private final RubricMongoRepository repository;

    @Autowired
    public RubricMongoService(RubricMongoRepository repository) {
        this.repository = repository;
    }

    public Rubric addNewRubric(Rubric rubric) {
        return repository.save(rubric);
    }
}
