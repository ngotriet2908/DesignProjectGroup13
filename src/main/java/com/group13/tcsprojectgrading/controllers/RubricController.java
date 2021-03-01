package com.group13.tcsprojectgrading.controllers;

import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.repositories.rubric.RubricMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/courses/{c_id}/projects/{p_id}/rubric")
public class RubricController {
    private final RubricMongoRepository repository;

    @Autowired
    public RubricController(RubricMongoRepository repository) {
        this.repository = repository;
    }

    @PostMapping("")
    public Rubric newRubric(@RequestBody Rubric newRubric) {
        System.out.println("Creating a rubric...");
        return repository.save(newRubric);
    }

    @GetMapping("")
    public Rubric getRubric() {
        System.out.println("Getting the rubric...");
        Rubric rubric = repository.findAll().get(0);
        return rubric;
    }
}
