import { SET_AUTH_STATE } from "./actionTypes";
import {SAVE_RUBRIC} from "./actionTypes";

export const setAuthState = signedIn => ({
  type: SET_AUTH_STATE,
  payload: { signedIn }
});
