package com.group13.tcsprojectgrading.repositories.grading;

import com.group13.tcsprojectgrading.models.grading.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

}
