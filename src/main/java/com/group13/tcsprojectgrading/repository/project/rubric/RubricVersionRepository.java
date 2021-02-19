package com.group13.tcsprojectgrading.repository.project.rubric;

import com.group13.tcsprojectgrading.model.project.rubric.Rubric;
import com.group13.tcsprojectgrading.model.project.rubric.RubricVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RubricVersionRepository extends JpaRepository<RubricVersion, Long> {

    public List<RubricVersion> findRubricVersionByRubric(Rubric rubric);
}
