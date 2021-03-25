package com.group13.tcsprojectgrading.services.graders;

import com.group13.tcsprojectgrading.models.Flag;
import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.GraderId;
import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.repositories.GraderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class GraderService {
    private final GraderRepository repository;

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

    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Grader> getGraderFromProject(Project project) {
        return repository.findGraderByProject(project);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public void deleteGrader(Grader grader) {
        repository.delete(grader);
    }

    @Transactional(value = Transactional.TxType.MANDATORY)
    public Grader getGraderFromGraderId(String userId, Project project) {
        return repository.findById(new GraderId(userId, project.getProjectCompositeKey())).orElse(null);
    }
}
