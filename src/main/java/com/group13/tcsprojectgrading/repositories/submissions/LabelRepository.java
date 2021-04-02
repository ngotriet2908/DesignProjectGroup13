package com.group13.tcsprojectgrading.repositories.submissions;

import com.group13.tcsprojectgrading.models.submissions.Label;
import com.group13.tcsprojectgrading.models.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabelRepository extends JpaRepository<Label, Long> {

    Label findByNameAndProject(String name, Project project);
    List<Label> findByProject(Project project);
}
