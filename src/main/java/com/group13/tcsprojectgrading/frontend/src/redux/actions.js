import { SET_AUTH_STATE } from "./actionTypes";

export const setAuthState = signedIn => ({
  type: SET_AUTH_STATE,
  payload: { signedIn }
});
