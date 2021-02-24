import { SET_AUTH_STATE } from "../actionTypes";

const initialState = {
  signedIn: false,
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
  default:
    return state;
  }
}