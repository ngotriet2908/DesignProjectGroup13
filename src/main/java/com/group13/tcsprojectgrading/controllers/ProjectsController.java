package com.group13.tcsprojectgrading.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group13.tcsprojectgrading.models.project.ProjectNew;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.repositories.rubric.RubricMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{c_id}/projects/")
public class ProjectsController {
    private final RubricMongoRepository repository;

    @Autowired
    public ProjectsController(RubricMongoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{p_id}")
    public ResponseEntity<String> getProject() throws JsonProcessingException {
        List<Rubric> rubric = repository.findAll();

        if (rubric.size() == 0) {
            return new ResponseEntity<>("{\"rubric\": null}", HttpStatus.OK);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            String rubricString = objectMapper.writeValueAsString(rubric.get(0));
            String response = "{\"rubric\":" + rubricString + "}";
            System.out.println(response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}

