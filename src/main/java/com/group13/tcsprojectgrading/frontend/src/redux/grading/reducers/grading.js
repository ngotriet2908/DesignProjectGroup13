import {ALTER_GRADE, ALTER_TEMP_ASSESSMENT_GRADE, SAVE_TEMP_ASSESSMENT} from "../actionTypes";
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
  case ALTER_GRADE: {
    console.log(state.assessment)
    const grades = alterGrade(state.assessment, action.payload.criterionId, action.payload.newGrade)
    console.log(grades)

    return {
      ...state,
      assessment: grades
    };
  }
  default:
    return state;
  }
}