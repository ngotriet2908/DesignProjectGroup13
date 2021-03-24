package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.Project;
import com.group13.tcsprojectgrading.models.ProjectId;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, ProjectId> {
    @Transactional(value = Transactional.TxType.MANDATORY)
    public List<Project> findProjectsByCourseId(String courseId);
}
