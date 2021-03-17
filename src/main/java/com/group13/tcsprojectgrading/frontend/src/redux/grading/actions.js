import {
  ALTER_GRADE, ALTER_TEMP_ASSESSMENT_GRADE, SAVE_ASSESSMENT, SAVE_TEMP_ASSESSMENT, SET_ACTIVE,
} from "./actionTypes";

// temporary assessment
export const saveTempAssessment = (tempAssessment) => ({
  type: SAVE_TEMP_ASSESSMENT,
  payload: tempAssessment
})

export const alterTempAssessmentGrade = (criterionId, newGrade) => ({
  type: ALTER_TEMP_ASSESSMENT_GRADE,
  payload: {criterionId, newGrade}
})


// stored assessment
export const saveAssessment = (assessment) => ({
  type: SAVE_ASSESSMENT,
  payload: assessment
})

export const alterGrade = (criterionId, newGrade) => ({
  type: ALTER_GRADE,
  payload: {criterionId, newGrade}
})

export const setActive = (criterionId, key) => ({
  type: SET_ACTIVE,
  payload: {criterionId, key}
})








