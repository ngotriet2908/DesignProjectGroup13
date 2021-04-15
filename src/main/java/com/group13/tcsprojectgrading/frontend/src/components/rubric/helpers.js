import {v4 as uuidv4} from "uuid";

export const isCriterion = (type) => {
  return type === "1";
}

export const isBlock = (type) => {
  return type === "0";
}

export const findCriteria = (children) => {
  return children.find(child => {
    return isCriterion(child.content.type);
  });
}

export const findBlocks = (children) => {
  return children.find(child => {
    return isBlock(child.content.type);
  });
}

// json patch
export const OPERATION = {
  add: "add",
  remove: "remove",
  replace: "replace",
  move: "move",
  copy: "copy",
  test: "test"
}

export function isSimilarUpdate(a, b) {
  return a.op === b.op && a.path === b.path;
}

export function createPatch(operation, path, value) {
  if (operation === OPERATION.remove) {
    return {
      op: operation,
      path: path,
    }
  } else {
    return {
      op: operation,
      path: path,
      value: value,
    }
  }
}

export const createNewBlock = (props, path, id, childrenLength) => {
  // create new section
  let newBlock = {
    content: {
      id: uuidv4(),
      type: "0",
      title: "Default section's title",
    },
    children: []
  }

  props.addBlock(id, newBlock, path + "/children/" + childrenLength);
  // open the new section
  props.setSelectedElement(newBlock.content.id);
  // set current path to the new section's path
  props.setCurrentPath(path + "/children/" + childrenLength);
}

export const createNewCriterion = (props, path, id, childrenLength) => {
  // create new criterion
  let newCriterion = {
    content: {
      id: uuidv4(),
      type: "1",
      title: "Default criterion's title",
      text: "You can edit this text in the edit mode.",
      grade: {
        min: 1,
        max: 10,
        step: 1,
        weight: 1.0
      },
    }
  }

  props.addCriterion(id, newCriterion, path + "/children/" + childrenLength);
  // open the new criterion
  props.setSelectedElement(newCriterion.content.id);
  // set current path to the new section's path
  props.setCurrentPath(path + "/children/" + childrenLength);
}

export const removeElement = (props) => {
  console.log("P");
  console.log(props);

  props.deleteElement(props.data.content.id, props.path);
  // TODO go to parent
  props.setCurrentPath("");
  props.setSelectedElement(props.rubric.id);
}

export const removeAll = (props) => {
  props.deleteAllElements();
  props.setCurrentPath("");
  props.setSelectedElement(props.rubric.id);
}






