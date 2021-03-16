import {ALTER_GRADE, ALTER_TEMP_ASSESSMENT_GRADE, SAVE_ASSESSMENT, SAVE_TEMP_ASSESSMENT} from "../actionTypes";
import {alterGrade, alterTempGrade} from "../functions";

const initialState = {
  assessment: {},
  tempAssessment: {}
};

export default function(state = initialState, action) {
  switch (action.type) {
  case SAVE_TEMP_ASSESSMENT: {
    return {
      ...state,
      tempAssessment: action.payload
    };
  }
  case ALTER_TEMP_ASSESSMENT_GRADE: {
    const grades = alterTempGrade(state.tempAssessment, action.payload.criterionId, action.payload.newGrade)

    return {
      ...state,
      tempAssessment: grades
    };
  }
  case SAVE_ASSESSMENT: {
    return {
      ...state,
      assessment: action.payload
    };
  }
  case ALTER_GRADE: {
    const grades = alterGrade(state.assessment.grades, action.payload.criterionId, action.payload.newGrade)

    return {
      ...state,
      assessment: {
        ...state.assessment,
        grades: grades
      }
    };
  }
  default:
    return state;
  }
}