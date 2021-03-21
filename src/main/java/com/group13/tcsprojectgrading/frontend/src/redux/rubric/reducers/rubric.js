import {
  ADD_BLOCK, ADD_CRITERION,
  ALTER_CRITERION_TEXT, ALTER_CRITERION_GRADE,
  ALTER_TITLE, DELETE_ALL_ELEMENTS, DELETE_ELEMENT, DELETE_RUBRIC, PUSH_RUBRIC_PATH,
  SAVE_RUBRIC, SAVE_TEMP_RUBRIC,
  SET_SELECTED_ELEMENT, EDITING_RUBRIC, SET_CURRENT_PATH, RESET_UPDATES
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
import {createPatch, isSimilarUpdate, OPERATION} from "../../../components/rubric/helpers";

const initialState = {
  selectedElement: null,
  rubric: null,
  rubricTemp: null,
  isEditing: false,
  updates: [],
  currentPath: ""
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
    const update = createPatch(OPERATION.add, action.payload.path, action.payload.newBlock);

    let updates = [...state.updates]
    updates.push(update);

    return {
      ...state,
      rubric: newRubric,
      updates: updates
    };
  }
  case ADD_CRITERION: {
    const newRubric = addCriterion(state.rubric, action.payload.id, action.payload.newCriterion)
    const update = createPatch(OPERATION.add, action.payload.path, action.payload.newCriterion);

    let updates = [...state.updates]
    updates.push(update);

    return {
      ...state,
      rubric: newRubric,
      updates: updates
    };
  }
  case DELETE_ELEMENT: {
    const newRubric = deleteElement(state.rubric, action.payload.id, action.payload.path)
    const update = createPatch(OPERATION.remove, action.payload.path);
    let updates = [...state.updates]

    if (updates.length > 0) {
      let last = updates[updates.length - 1]
      if (isSimilarUpdate(last, update)) {
        updates[updates.length - 1] = update
      } else {
        updates.push(update);
      }
    } else {
      updates.push(update);
    }

    return {
      ...state,
      rubric: newRubric,
      updates: updates
    };
  }
  case DELETE_ALL_ELEMENTS: {
    const newRubric = deleteAllElements(state.rubric)
    const update = createPatch(OPERATION.replace, "/children", []);
    let updates = [...state.updates]

    if (updates.length > 0) {
      let last = updates[updates.length - 1]
      if (isSimilarUpdate(last, update)) {
        updates[updates.length - 1] = update
      } else {
        updates.push(update);
      }
    } else {
      updates.push(update);
    }

    return {
      ...state,
      rubric: newRubric,
      updates: updates
    };
  }
  case ALTER_TITLE: {
    const newRubric = changeTitle(state.rubric, action.payload.id, action.payload.newTitle)
    const update = createPatch(OPERATION.replace, action.payload.path, action.payload.newTitle);
    let updates = [...state.updates]

    if (updates.length > 0) {
      let last = updates[updates.length - 1]
      if (isSimilarUpdate(last, update)) {
        console.log("don't push");
        updates[updates.length - 1] = update
      } else {
        console.log("push");
        updates.push(update);
      }
    } else {
      console.log("push");
      updates.push(update);
    }

    return {
      ...state,
      rubric: newRubric,
      updates: updates
    };
  }
  case ALTER_CRITERION_TEXT: {
    const newRubric = changeText(state.rubric, action.payload.id, action.payload.newText)
    const update = createPatch(OPERATION.replace, action.payload.path, action.payload.newText);
    let updates = [...state.updates]

    if (updates.length > 0) {
      let last = updates[updates.length - 1]
      if (isSimilarUpdate(last, update)) {
        updates[updates.length - 1] = update
      } else {
        updates.push(update);
      }
    } else {
      updates.push(update);
    }

    return {
      ...state,
      rubric: newRubric,
      updates: updates
    };
  }
  case ALTER_CRITERION_GRADE: {
    const newRubric = changeGrade(state.rubric, action.payload.id, action.payload.newGrade)
    const update = createPatch(OPERATION.replace, action.payload.path, action.payload.newGrade);
    let updates = [...state.updates]

    if (updates.length > 0) {
      let last = updates[updates.length - 1]
      if (isSimilarUpdate(last, update)) {
        updates[updates.length - 1] = update
      } else {
        updates.push(update);
      }
    } else {
      updates.push(update);
    }

    return {
      ...state,
      rubric: newRubric,
      updates: updates
    };
  }
  case SET_CURRENT_PATH: {
    return {
      ...state,
      currentPath: action.payload
    };
  }
  case RESET_UPDATES: {
    return {
      ...state,
      updates: []
    };
  }
  default:
    return state;
  }
}






