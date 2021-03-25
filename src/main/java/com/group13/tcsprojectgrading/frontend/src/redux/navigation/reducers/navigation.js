import {SET_CURRENT_LOCATION} from "../actionTypes";

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
  })

const initialState = {
  location: LOCATIONS.home
};

export default function(state = initialState, action) {
  switch (action.type) {
  case SET_CURRENT_LOCATION: {
    return {
      ...state,
      location: action.payload
    };
  }
  default:
    return state;
  }
}






