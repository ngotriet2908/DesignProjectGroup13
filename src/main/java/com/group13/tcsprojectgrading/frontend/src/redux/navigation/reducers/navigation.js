import {SET_CURRENT_LOCATION} from "../actionTypes";

export const LOCATIONS = Object.freeze(
  {
    "home": 1,
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






