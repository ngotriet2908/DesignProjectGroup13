import {
  DELETE_CURRENT_COURSE,
  SAVE_CURRENT_COURSE
} from "./actionTypes";

export const saveCurrentCourse = (currentCourse) => ({
  type: SAVE_CURRENT_COURSE,
  payload: currentCourse
})

export const deleteCurrentCourse = () => ({
  type: DELETE_CURRENT_COURSE,
})








