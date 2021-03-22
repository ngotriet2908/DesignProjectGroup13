package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Flag;
import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FlagRepository extends JpaRepository<Flag, UUID> {

    public Flag findFlagByNameAndProject(String name, Project project);
    public List<Flag> findFlagsByProject(Project project);
}
