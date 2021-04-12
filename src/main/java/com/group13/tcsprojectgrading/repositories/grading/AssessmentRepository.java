package com.group13.tcsprojectgrading.repositories.grading;

import com.group13.tcsprojectgrading.models.grading.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;


public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Assessment> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Assessment> findAssessmentById(Long id);
}
