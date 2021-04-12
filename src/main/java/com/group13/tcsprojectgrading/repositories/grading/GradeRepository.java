package com.group13.tcsprojectgrading.repositories.grading;

import com.group13.tcsprojectgrading.models.grading.Assessment;
import com.group13.tcsprojectgrading.models.grading.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long>  {
    @Modifying
    @Query(value="UPDATE grade " +
            "SET is_active=false " +
            "WHERE grade.assessment_id=?1 AND grade.criterion_id=?2", nativeQuery=true)
    void deactivateAllGrades(Long assessmentId, String criterionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Grade> findGradesByAssessment_IdAndCriterionId(Long assessmentId, String criterionId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    public List<Grade> findGradesByAssessmentAndIsActiveIsTrue(Assessment assessment);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Grade> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void deleteAllByCriterionId(String criterionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Grade> findGradeById(Long id);
}
