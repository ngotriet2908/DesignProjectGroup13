import {SET_CURRENT_COURSE_AND_PROJECT, SET_CURRENT_LOCATION} from "./actionTypes";


export const setCurrentLocation = (location) => ({
  type: SET_CURRENT_LOCATION,
  payload: location
})

export const setCurrentCourseAndProject = (courseId, projectId) => ({
  type: SET_CURRENT_COURSE_AND_PROJECT,
  payload: {
    courseId,
    projectId
  }
})
