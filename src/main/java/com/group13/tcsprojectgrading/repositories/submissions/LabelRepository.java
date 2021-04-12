package com.group13.tcsprojectgrading.repositories.submissions;

import com.group13.tcsprojectgrading.models.submissions.Label;
import com.group13.tcsprojectgrading.models.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.List;

public interface LabelRepository extends JpaRepository<Label, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Label findByNameAndProject(String name, Project project);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Label> findByProject(Project project);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Label> findByProjectId(Long project_id);
}
