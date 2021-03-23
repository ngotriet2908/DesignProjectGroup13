package com.group13.tcsprojectgrading.grading;

import com.group13.tcsprojectgrading.models.Assessment;
import com.group13.tcsprojectgrading.models.grading.CriterionGrade;
import com.group13.tcsprojectgrading.models.grading.Grade;
import com.group13.tcsprojectgrading.models.rubric.Element;
import com.group13.tcsprojectgrading.models.rubric.Rubric;
import com.group13.tcsprojectgrading.models.rubric.RubricContent;
import com.group13.tcsprojectgrading.models.rubric.RubricGrade;
import com.group13.tcsprojectgrading.repositories.grading.AssessmentRepository;
import com.group13.tcsprojectgrading.services.AssessmentLinkerService;
import com.group13.tcsprojectgrading.services.grading.AssessmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class AssessmentServiceTest {
   @Mock
   private AssessmentRepository repository;

   @Mock
   private AssessmentLinkerService linkerService;

   @InjectMocks
   private AssessmentService assessmentService;

   @Test
   void finalGradeCalculated() {
      Rubric rubric = new Rubric("111");
      Element criterion1 = new Element(new RubricContent("1", RubricContent.CRITERION_TYPE, "test", "test", new RubricGrade(0, 10, 1, 1)));
      Element criterion2 = new Element(new RubricContent("2", RubricContent.CRITERION_TYPE, "test", "test", new RubricGrade(0, 10, 1, 1.5)));
      rubric.setChildren(Arrays.asList(criterion1, criterion2));
      Grade grade1 = new Grade();
      grade1.setGrade(5);
      Grade grade2 = new Grade();
      grade2.setGrade(7);
      CriterionGrade criterionGrade1 = new CriterionGrade(0, Collections.singletonList(grade1));
      CriterionGrade criterionGrade2 = new CriterionGrade(0, Collections.singletonList(grade2));
      Map<String, CriterionGrade> gradeMap = Map.of("1", criterionGrade1, "2", criterionGrade2);
      Assessment assessment = new Assessment(new UUID(523, 61), gradeMap);

      //Grade of 5 with weight 1 and 7 with weight 1.5, should be 15.5.
      assertThat(assessmentService.calculateFinalGrade(rubric, assessment)).isEqualTo(15.5);
   }
}