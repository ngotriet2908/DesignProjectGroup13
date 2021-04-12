import {SET_CURRENT_COURSE_AND_PROJECT, SET_CURRENT_LOCATION, TOGGLE_SIDEBAR_HIDDEN} from "../actionTypes";

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
    // "participants": 10,
    // "participant": 11,
    "students": 10,
    "student": 11,
  })

const initialState = {
  location: LOCATIONS.home,
  course: null,
  project: null,
  sidebarHidden: true,
};

export default function(state = initialState, action) {
  switch (action.type) {
  case SET_CURRENT_LOCATION: {
    return {
      ...state,
      location: action.payload
    };
  }
  case TOGGLE_SIDEBAR_HIDDEN: {
    return {
      ...state,
      sidebarHidden: !state.sidebarHidden
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






