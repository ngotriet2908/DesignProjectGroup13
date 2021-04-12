import {SET_CURRENT_COURSE_AND_PROJECT, SET_CURRENT_LOCATION, TOGGLE_SIDEBAR_HIDDEN} from "./actionTypes";


export const setCurrentLocation = (location) => ({
  type: SET_CURRENT_LOCATION,
  payload: location
})

export const toggleSidebarHidden = () => ({
  type: TOGGLE_SIDEBAR_HIDDEN
})

export const setCurrentCourseAndProject = (courseId, projectId) => ({
  type: SET_CURRENT_COURSE_AND_PROJECT,
  payload: {
    courseId,
    projectId
  }
})
