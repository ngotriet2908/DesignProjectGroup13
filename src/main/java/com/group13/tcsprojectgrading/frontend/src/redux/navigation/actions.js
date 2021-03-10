import {SET_CURRENT_LOCATION} from "./actionTypes";


export const setCurrentLocation = (location) => ({
  type: SET_CURRENT_LOCATION,
  payload: location
})