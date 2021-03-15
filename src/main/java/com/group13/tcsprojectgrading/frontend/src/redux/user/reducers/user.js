import {REMOVE_USER, SAVE_USER, SET_AUTH_STATE} from "../actionTypes";

const initialState = {
  signedIn: false,
  role: 'ta',
  user: null,
};

export default function(state = initialState, action) {
  switch (action.type) {
  case SET_AUTH_STATE: {
    const { signedIn } = action.payload;
    return {
      ...state,
      signedIn: signedIn,
    };
  }
  case SAVE_USER: {
    return {
      ...state,
      user: action.payload,
      role: 'teacher',
    };
  }
  case REMOVE_USER: {
    return {
      ...state,
      user: null,
    };
  }
  default:
    return state;
  }
}