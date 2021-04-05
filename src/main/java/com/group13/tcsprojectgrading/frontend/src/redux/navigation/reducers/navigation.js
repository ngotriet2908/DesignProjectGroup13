import {SET_CURRENT_COURSE_AND_PROJECT, SET_CURRENT_LOCATION} from "../actionTypes";

export const LOCATIONS = Object.freeze(
  {
    "home": 1,
    "course": 2,
    "project": 3,
    "rubric": 4,
    "grading": 5,
    "graders": 6,
    "submissions": 7,
    "submission": 8,
    "settings": 9,
    "students": 10,
  })

const initialState = {
  location: LOCATIONS.home,
  course: null,
  project: null,
};

export default function(state = initialState, action) {
  switch (action.type) {
  case SET_CURRENT_LOCATION: {
    return {
      ...state,
      location: action.payload
    };
  }
  case SET_CURRENT_COURSE_AND_PROJECT: {
    return {
      ...state,
      course: action.payload.courseId,
      project: action.payload.projectId
    };
  }
  default:
    return state;
  }
}






