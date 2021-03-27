package com.group13.tcsprojectgrading.repositories.graders;

import com.group13.tcsprojectgrading.models.graders.Grader;
import com.group13.tcsprojectgrading.models.graders.GraderId;
import com.group13.tcsprojectgrading.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GraderRepository extends JpaRepository<Grader, GraderId> {

    public List<Grader> findGraderByProject(Project project);
}
