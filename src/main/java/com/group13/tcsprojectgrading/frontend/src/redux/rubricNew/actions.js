import {
  ADD_BLOCK, ADD_CRITERION,
  ALTER_CRITERION_TEXT, ALTER_GRADE,
  ALTER_TITLE, DELETE_ALL_ELEMENTS, DELETE_ELEMENT, DELETE_RUBRIC, PUSH_RUBRIC_PATH,
  SAVE_RUBRIC, SAVE_TEMP_RUBRIC,
  SET_SELECTED_ELEMENT,
  EDITING_RUBRIC
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

export const addBlock = (id, newBlock) => ({
  type: ADD_BLOCK,
  payload: {
    id,
    newBlock
  }
})

export const addCriterion = (id, newCriterion) => ({
  type: ADD_CRITERION,
  payload: {
    id,
    newCriterion
  }
})

export const deleteElement = (id) => ({
  type: DELETE_ELEMENT,
  payload: {
    id
  }
})

export const deleteAllElements= () => ({
  type: DELETE_ALL_ELEMENTS,
})


export const alterTitle = (id, newTitle) => ({
  type: ALTER_TITLE,
  payload: {
    id, newTitle
  }
})

export const alterCriterionText = (id, newText) => ({
  type: ALTER_CRITERION_TEXT,
  payload: {
    id, newText
  }
})

export const alterGrade = (id, newGrade) => ({
  type: ALTER_GRADE,
  payload: {
    id, newGrade
  }
})

// rubric breadcrumbs
export const pushRubricPath = (title) => ({
  type: PUSH_RUBRIC_PATH,
  payload: title
})







