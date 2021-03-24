package com.group13.tcsprojectgrading.repositories;

import com.group13.tcsprojectgrading.models.RubricHistoryLinker;
import com.group13.tcsprojectgrading.models.rubric.RubricHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RubricHistoryLinkerRepository extends JpaRepository<RubricHistoryLinker, String> {
}
