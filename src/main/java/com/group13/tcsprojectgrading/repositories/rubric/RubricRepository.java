package com.group13.tcsprojectgrading.repositories.rubric;

import java.util.List;

import com.group13.tcsprojectgrading.models.rubric.Rubric;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RubricRepository extends MongoRepository<Rubric, String> {

//    public Rubric findByFirstName(String firstName);
//    public List<Customer> findByLastName(String lastName);
//    public void deleteByProjectId() {}
//    void deleteByProjectId(String projectId);
//
//    Rubric getByProjectId(String projectId);

    void deleteById(String id);
    Rubric getById(String id);
}
