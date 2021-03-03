import {REMOVE_USER, SAVE_USER, SET_AUTH_STATE} from "./actionTypes";

export const setAuthState = signedIn => ({
  type: SET_AUTH_STATE,
  payload: { signedIn }
});

export const saveUser = user => ({
  type: SAVE_USER,
  payload: user
});

export const removeUser = () => ({
  type: REMOVE_USER,
});
