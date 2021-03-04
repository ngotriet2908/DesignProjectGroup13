package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.GraderId;
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
        return repository.save(grader);
    }

    public List<Grader> getGraderFromId(String courseId, String projectId) {
        return repository.findGraderByCourseIdAndProjectId(courseId, projectId);
    }

    public void deleteGrader(Grader grader) {
        repository.delete(grader);
    }

    public Grader getGraderFromGraderId(String userId, String courseId, String projectId) {
        return repository.findById(new GraderId(userId, courseId, projectId)).orElse(null);
    }
}
