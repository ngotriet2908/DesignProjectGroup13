import {
  APPEND_BLOCK,
  REMOVE_BLOCK,
  REMOVE_RUBRIC,
  SAVE_RUBRIC,
  APPEND_CRITERION,
  REMOVE_CRITERION,
  ALTER_CRITERION,
  ALTER_BLOCK_TITLE,
  REORDER_CRITERION,
  MOVE_CRITERION,
  EDITING_RUBRIC,
  SAVE_RUBRIC_BACKUP
} from "./actionTypes";

export const saveRubric = (rubric) => ({
  type: SAVE_RUBRIC,
  payload: { rubric }
})

export const removeRubric = () => ({
  type: REMOVE_RUBRIC,
})

export const appendBlock = (block) => ({
  type: APPEND_BLOCK,
  payload: block
})

export const removeBlock = (blockId) => ({
  type: REMOVE_BLOCK,
  payload: {
    blockId
  }
})

export const addCriterion = (criterion, blockId) => ({
  type: APPEND_CRITERION,
  payload: {
    criterion,
    blockId
  }
})

export const removeCriterion = (blockId, criterionId) => ({
  type: REMOVE_CRITERION,
  payload: {
    criterionId,
    blockId
  }
})

export const alterCriterion = (criterion, blockId, criterionId) => ({
  type: ALTER_CRITERION,
  payload: {
    criterion,
    blockId,
    criterionId
  }
})

export const alterBlockTitle = (title, blockId) => ({
  type: ALTER_BLOCK_TITLE,
  payload: {
    title,
    blockId,
  }
})

export const reorderCriterion = (blockId, sourceIndex, destinationIndex) => ({
  type: REORDER_CRITERION,
  payload: {
    blockId,
    sourceIndex,
    destinationIndex
  }
})

export const moveCriterion = (sourceBlockId, destinationBlockId, sourceIndex, destinationIndex) => ({
  type: MOVE_CRITERION,
  payload: {
    sourceBlockId,
    destinationBlockId,
    sourceIndex,
    destinationIndex
  }
})

export const setEditingRubric = (isEditing) => ({
  type: EDITING_RUBRIC,
  payload: {
    isEditing
  }
})

export const saveRubricBackup = (rubricCopy) => ({
  type: SAVE_RUBRIC_BACKUP,
  payload: { rubricCopy }
})










