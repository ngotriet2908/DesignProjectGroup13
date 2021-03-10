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