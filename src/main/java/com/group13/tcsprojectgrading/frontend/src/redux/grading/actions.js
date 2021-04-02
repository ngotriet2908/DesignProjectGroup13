import {
  ALTER_TEMP_ASSESSMENT_GRADE,
  SAVE_ASSESSMENT,
  SAVE_GRADE,
  SAVE_TEMP_ASSESSMENT,
  SET_ACTIVE,
} from "./actionTypes";

import groupBy from 'lodash/groupBy';

// temporary assessment actions

export const saveTempAssessment = (tempAssessment) => ({
  type: SAVE_TEMP_ASSESSMENT,
  payload: tempAssessment
})

export const alterTempAssessmentGrade = (criterionId, newGrade) => ({
  type: ALTER_TEMP_ASSESSMENT_GRADE,
  payload: {criterionId, newGrade}
})


// stored assessment actions

export const saveAssessment = (assessment) => {

  // assessment.grades = assessment.grades.filter(grade => {
  //   return grade.criterionId === criterionId;
  // });

  assessment.grades = groupBy(assessment.grades, (grade) => {
    return grade.criterionId;
  });

  return({
    type: SAVE_ASSESSMENT,
    payload: assessment
  })
}

export const saveGrade = (newGrade) => ({
  type: SAVE_GRADE,
  payload: {newGrade}
})

export const setActive = (criterionId, key) => ({
  type: SET_ACTIVE,
  payload: {criterionId, key}
})








