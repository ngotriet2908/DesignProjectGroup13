package com.group13.tcsprojectgrading.repositories.project.rubric;

import com.group13.tcsprojectgrading.models.project.rubric.Rubric;
import com.group13.tcsprojectgrading.models.project.rubric.RubricVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RubricVersionRepository extends JpaRepository<RubricVersion, Long> {

    public List<RubricVersion> findRubricVersionByRubric(Rubric rubric);
}
