import {REMOVE_USER, REMOVE_USER_SELF, SAVE_USER, SAVE_USER_SELF, SET_AUTH_STATE} from "./actionTypes";

export const setAuthState = signedIn => ({
  type: SET_AUTH_STATE,
  payload: { signedIn }
});

export const saveUserSelf = user => ({
  type: SAVE_USER_SELF,
  payload: user
});

export const removeUserSelf = () => ({
  type: REMOVE_USER_SELF,
});

export const saveUser = user => ({
  type: SAVE_USER,
  payload: user
});

export const removeUser = (userId) => ({
  type: REMOVE_USER,
  payload: userId
});
