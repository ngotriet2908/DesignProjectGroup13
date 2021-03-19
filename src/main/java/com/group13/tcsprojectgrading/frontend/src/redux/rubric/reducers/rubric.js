import {
  ADD_BLOCK, ADD_CRITERION,
  ALTER_CRITERION_TEXT, ALTER_CRITERION_GRADE,
  ALTER_TITLE, DELETE_ALL_ELEMENTS, DELETE_ELEMENT, DELETE_RUBRIC, PUSH_RUBRIC_PATH,
  SAVE_RUBRIC, SAVE_TEMP_RUBRIC,
  SET_SELECTED_ELEMENT, EDITING_RUBRIC
} from "../actionTypes";
import {
  addBlock,
  addCriterion,
  changeGrade,
  changeText,
  changeTitle,
  deleteAllElements,
  deleteElement
} from "../functions";
import {isSimilarUpdate, OPERATION, TYPE} from "../updates";

const initialState = {
  selectedElement: null,
  rubric: null,
  rubricTemp: null,
  rubricPath: [],
  isEditing: false,
  updates: []
};

export default function(state = initialState, action) {
  switch (action.type) {
  case SET_SELECTED_ELEMENT: {
    return {
      ...state,
      selectedElement: action.payload
    };
  }
  case SAVE_RUBRIC: {
    return {
      ...state,
      rubric: action.payload
    };
  }
  case DELETE_RUBRIC: {
    return {
      ...state,
      rubric: null,
      rubricTemp: null
    };
  }
  case EDITING_RUBRIC: {
    return {
      ...state,
      isEditing: action.payload.isEditing,
    };
  }
  case SAVE_TEMP_RUBRIC: {
    return {
      ...state,
      rubricTemp: action.payload
    };
  }
  case ADD_BLOCK: {
    const newRubric = addBlock(state.rubric, action.payload.id, action.payload.newBlock)

    return {
      ...state,
      rubric: newRubric
    };
  }
  case ADD_CRITERION: {
    const newRubric = addCriterion(state.rubric, action.payload.id, action.payload.newCriterion)

    return {
      ...state,
      rubric: newRubric
    };
  }
  case DELETE_ELEMENT: {
    const newRubric = deleteElement(state.rubric, action.payload.id)

    return {
      ...state,
      rubric: newRubric
    };
  }
  case DELETE_ALL_ELEMENTS: {
    const newRubric = deleteAllElements(state.rubric)

    return {
      ...state,
      rubric: newRubric
    };
  }
  case ALTER_TITLE: {
    const newRubric = changeTitle(state.rubric, action.payload.id, action.payload.newTitle)

    return {
      ...state,
      rubric: newRubric
    };
  }
  case ALTER_CRITERION_TEXT: {
    const newRubric = changeText(state.rubric, action.payload.id, action.payload.newText)
    const updates = [...state.updates]

    const newUpdate = {
      op: OPERATION.replace,
      type: TYPE.criterionText,
      id: action.payload.id,
      value: action.payload.newText
    }

    if (updates.length > 0) {
      const lastUpdate = updates[updates.length - 1]

      if (isSimilarUpdate(lastUpdate, newUpdate)) {
        updates[updates.length - 1] = newUpdate
      } else {
        updates.push(newUpdate)
      }
    } else {
      updates.push(newUpdate)
    }

    return {
      ...state,
      rubric: newRubric,
      updates: updates
    };
  }
  case ALTER_CRITERION_GRADE: {
    const newRubric = changeGrade(state.rubric, action.payload.id, action.payload.newGrade)

    const updates = [...state.updates]

    const newUpdate = {
      op: OPERATION.replace,
      type: TYPE.criterionGrade,
      id: action.payload.id,
      value: action.payload.newGrade
    }

    if (updates.length > 0) {
      const lastUpdate = updates[updates.length - 1]

      if (isSimilarUpdate(lastUpdate, newUpdate)) {
        updates[updates.length - 1] = newUpdate
      } else {
        updates.push(newUpdate)
      }
    } else {
      updates.push(newUpdate)
    }

    return {
      ...state,
      rubric: newRubric
    };
  }
  default:
    return state;
  }
}






