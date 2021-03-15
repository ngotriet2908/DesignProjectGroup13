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

const initialState = {
  selectedElement: null,
  rubric: null,
  rubricTemp: null,
  rubricPath: [],
  isEditing: false,
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

    return {
      ...state,
      rubric: newRubric
    };
  }
  case ALTER_CRITERION_GRADE: {
    const newRubric = changeGrade(state.rubric, action.payload.id, action.payload.newGrade)

    return {
      ...state,
      rubric: newRubric
    };
  }
  default:
    return state;
  }
}






