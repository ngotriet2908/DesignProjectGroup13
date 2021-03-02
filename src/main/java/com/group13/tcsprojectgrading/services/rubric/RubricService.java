package com.group13.tcsprojectgrading.services.rubric;

//import com.group13.tcsprojectgrading.models.project.Attachment;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
//import com.group13.tcsprojectgrading.repositories.project.AttachmentRepository;
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

    public Rubric addNewRubric(Rubric rubric) {
        return repository.save(rubric);
    }

    public List<Rubric> getAllRubrics() {
        return repository.findAll();
    }

    public void removeRubric() {
        // TODO removes all rubrics right now
        repository.deleteAll();
//        return repository.findAll();
    }
}
