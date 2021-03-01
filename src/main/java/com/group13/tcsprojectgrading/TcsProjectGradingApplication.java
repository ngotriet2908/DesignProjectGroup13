package com.group13.tcsprojectgrading;

import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.repositories.rubric.RubricMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;

@SpringBootApplication
//public class TcsProjectGradingApplication implements CommandLineRunner {
public class TcsProjectGradingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcsProjectGradingApplication.class, args);
    }

    @Autowired
    private RubricMongoRepository repository;

//    @Override
//    public void run(String... args) {
//        repository.deleteAll();
////        repository.save(new Rubric("v1", new ArrayList<>()));
//
//        // fetch all customers
////        System.out.println("Rubrics found with findAll():");
////        System.out.println("-------------------------------");
////        for (Rubric rubric : repository.findAll()) {
////            System.out.println(rubric);
////        }
////        System.out.println();
//    }
}
