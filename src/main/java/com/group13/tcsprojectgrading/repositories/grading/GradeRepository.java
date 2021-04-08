package com.group13.tcsprojectgrading.repositories.grading;

import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.grading.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long>  {
    @Modifying
    @Query(value="UPDATE grade " +
            "SET is_active=false " +
            "WHERE grade.assessment_id=?1 AND grade.criterion_id=?2", nativeQuery=true)
    void deactivateAllGrades(Long assessmentId, String criterionId);

    public List<Grade> findGradesByAssessmentAndIsActiveIsTrue(Assessment assessment);
}
