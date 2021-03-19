import {REMOVE_USER, REMOVE_USER_SELF, SAVE_USER, SAVE_USER_SELF, SET_AUTH_STATE} from "../actionTypes";
import {addUser, removeUser} from "../functions";

const initialState = {
  self: null,
  users: []
};

export default function(state = initialState, action) {
  switch (action.type) {
  case SAVE_USER_SELF: {
    return {
      ...state,
      self: action.payload,
    };
  }
  case REMOVE_USER_SELF: {
    return {
      ...state,
      self: null,
    };
  }
  case SAVE_USER: {
    let newUsers = addUser(state.users, action.payload.userId);

    return {
      ...state,
      users: newUsers,
    };
  }
  case REMOVE_USER: {
    let newUsers = removeUser(state.users, action.payload.userId);

    return {
      ...state,
      users: newUsers,
    };
  }
  default:
    return state;
  }
}