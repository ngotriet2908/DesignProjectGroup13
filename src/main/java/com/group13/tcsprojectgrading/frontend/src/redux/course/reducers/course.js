import {
  SAVE_COURSE
} from "../actionTypes";

const initialState = {
  course: null,
};

export default function(state = initialState, action) {
  switch (action.type) {
  case SAVE_COURSE: {
    return {
      ...state,
      ...action.payload,
    };
  }
  default:
    return state;
  }
}