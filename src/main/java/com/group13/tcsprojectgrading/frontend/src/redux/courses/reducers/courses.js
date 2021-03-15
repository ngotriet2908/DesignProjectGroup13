import {
  DELETE_CURRENT_COURSE,
  SAVE_CURRENT_COURSE
} from "../actionTypes";

const initialState = {
  currentCourse: null,
};

export default function(state = initialState, action) {
  switch (action.type) {
  case SAVE_CURRENT_COURSE: {
    return {
      ...state,
      currentCourse: action.payload
    };
  }
  case DELETE_CURRENT_COURSE: {
    return {
      ...state,
      currentCourse: null
    };
  }
  default:
    return state;
  }
}