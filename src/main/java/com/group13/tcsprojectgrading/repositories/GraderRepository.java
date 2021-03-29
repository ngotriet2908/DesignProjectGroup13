package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.GraderId;
import com.group13.tcsprojectgrading.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GraderRepository extends JpaRepository<Grader, GraderId> {

    List<Grader> findGraderByProject(Project project);
}
