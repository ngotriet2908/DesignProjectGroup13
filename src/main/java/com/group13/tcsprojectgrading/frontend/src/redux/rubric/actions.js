import {
  ADD_BLOCK, ADD_CRITERION,
  ALTER_CRITERION_TEXT, ALTER_CRITERION_GRADE,
  ALTER_TITLE, DELETE_ALL_ELEMENTS, DELETE_ELEMENT, DELETE_RUBRIC, PUSH_RUBRIC_PATH,
  SAVE_RUBRIC, SAVE_TEMP_RUBRIC,
  SET_SELECTED_ELEMENT,
  EDITING_RUBRIC, SET_CURRENT_PATH, RESET_UPDATES
} from "./actionTypes";

export const setSelectedElement = (element) => ({
  type: SET_SELECTED_ELEMENT,
  payload: element
})

export const saveRubric = (rubric) => ({
  type: SAVE_RUBRIC,
  payload: rubric
})

export const deleteRubric = () => ({
  type: DELETE_RUBRIC,
})

export const setEditingRubric = (isEditing) => ({
  type: EDITING_RUBRIC,
  payload: {
    isEditing
  }
})

export const saveRubricTemp = (rubric) => ({
  type: SAVE_TEMP_RUBRIC,
  payload: rubric
})

export const addBlock = (id, newBlock ,path) => ({
  type: ADD_BLOCK,
  payload: {
    id,
    newBlock,
    path
  }
})

export const addCriterion = (id, newCriterion, path) => ({
  type: ADD_CRITERION,
  payload: {
    id,
    newCriterion,
    path
  }
})

export const deleteElement = (id, path) => ({
  type: DELETE_ELEMENT,
  payload: {
    id, path
  }
})

export const deleteAllElements= () => ({
  type: DELETE_ALL_ELEMENTS,
})


export const alterTitle = (id, newTitle, path) => ({
  type: ALTER_TITLE,
  payload: {
    id, newTitle, path
  }
})

export const alterCriterionText = (id, newText, path) => ({
  type: ALTER_CRITERION_TEXT,
  payload: {
    id, newText, path
  }
})

export const alterGrade = (id, newGrade, path) => ({
  type: ALTER_CRITERION_GRADE,
  payload: {
    id, newGrade, path
  }
})

// json patch
export const setCurrentPath = (path) => ({
  type: SET_CURRENT_PATH,
  payload: path
})

export const resetUpdates = () => ({
  type: RESET_UPDATES,
})








