import {
  APPEND_CRITERION,
  APPEND_BLOCK,
  SAVE_RUBRIC,
  REMOVE_RUBRIC,
  REMOVE_CRITERION,
  ALTER_CRITERION,
  ALTER_BLOCK_TITLE,
  REORDER_CRITERION,
  MOVE_CRITERION,
  REMOVE_BLOCK,
  EDITING_RUBRIC,
  SAVE_RUBRIC_BACKUP
} from "../actionTypes";
import {
  alterBlockTitle,
  alterCriterion,
  appendCriterion,
  removeCriterion,
  reorderCriterion,
  moveCriterion,
  removeBlock
} from "../functions";

const initialState = {
  // rubric: null,
  // rubricCopy: null,
  // isEditing: false,
};

export default function(state = initialState, action) {
  switch (action.type) {
  case SAVE_RUBRIC: {
    return {
      ...state,
      ...action.payload,
    };
  }
  case REMOVE_RUBRIC: {
    return {
      ...state,
      rubric: null,
    };
  }
  case APPEND_BLOCK: {
    return {
      ...state,
      rubric: {
        ...state.rubric,
        blocks: [
          ...state.rubric.blocks,
          action.payload
        ]
      }
    };
  }
  case REMOVE_BLOCK: {
    let newBlockList = removeBlock(state.rubric.blocks, action.payload.blockId);
    return {
      ...state,
      rubric: {
        ...state.rubric,
        blocks: newBlockList
      }
    };
  }
  case APPEND_CRITERION: {
    let newBlockList = appendCriterion(state.rubric.blocks, action.payload.blockId, action.payload.criterion);
    return {
      ...state,
      rubric: {
        ...state.rubric,
        blocks: newBlockList
      }
    };
  }
  case REMOVE_CRITERION: {
    let newBlockList = removeCriterion(state.rubric.blocks, action.payload.blockId, action.payload.criterionId);
    return {
      ...state,
      rubric: {
        ...state.rubric,
        blocks: newBlockList
      }
    };
  }
  case ALTER_CRITERION: {
    let newBlockList = alterCriterion(state.rubric.blocks, action.payload.criterion, action.payload.criterionId, action.payload.blockId);
    return {
      ...state,
      rubric: {
        ...state.rubric,
        blocks: newBlockList
      }
    };
  }
  case ALTER_BLOCK_TITLE: {
    let newBlockList = alterBlockTitle(state.rubric.blocks, action.payload.title, action.payload.blockId);
    return {
      ...state,
      rubric: {
        ...state.rubric,
        blocks: newBlockList
      }
    };
  }
  case REORDER_CRITERION: {
    let newBlockList = reorderCriterion(
      state.rubric.blocks,
      action.payload.blockId,
      action.payload.sourceIndex,
      action.payload.destinationIndex
    );
    return {
      ...state,
      rubric: {
        ...state.rubric,
        blocks: newBlockList
      }
    };
  }
  case MOVE_CRITERION: {
    let newBlockList = moveCriterion(
      state.rubric.blocks,
      action.payload.sourceBlockId,
      action.payload.destinationBlockId,
      action.payload.sourceIndex,
      action.payload.destinationIndex
    );
    return {
      ...state,
      rubric: {
        ...state.rubric,
        blocks: newBlockList
      }
    };
  }
  case EDITING_RUBRIC: {
    return {
      ...state,
      isEditing: action.payload.isEditing,
    };
  }
  case SAVE_RUBRIC_BACKUP: {
    return {
      ...state,
      ...action.payload,
    };
  }
  default:
    return state;
  }
}