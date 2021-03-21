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





