export const appendCriterion = (blocks, blockId, criterion) => {
  let newBlockList = JSON.parse(JSON.stringify(blocks));

  newBlockList.map(block => {
    if (block.id === blockId) {
      let newBlockCriteria = [...block.criteria]
      newBlockCriteria.push(criterion);
      block.criteria = newBlockCriteria;
    }
  })

  return newBlockList;
}

export const removeCriterion = (blocks, blockId, criterionId) => {
  let newBlockList = JSON.parse(JSON.stringify(blocks));

  newBlockList.map(block => {
    if (block.id === blockId) {
      block.criteria = block.criteria.filter(criterion => {
        return (criterion.id !== criterionId)
      })
    }
  })

  return newBlockList;
}

export const removeBlock = (blocks, blockId) => {
  let newBlockList = JSON.parse(JSON.stringify(blocks));

  newBlockList = newBlockList.filter(block => {
    return (block.id !== blockId)
  })

  return newBlockList;
}

export const alterCriterion = (blocks, newCriterion, criterionId, blockId) => {
  let newBlockList = JSON.parse(JSON.stringify(blocks));

  newBlockList.map(block => {
    if (block.id === blockId) {
      block.criteria.map(criterion => {
        if (criterion.id === criterionId) {
          criterion.title = newCriterion.title;
          criterion.text = newCriterion.text;
        }
      })
    }
  })

  return newBlockList;
}

export const alterBlockTitle = (blocks, title, blockId) => {
  let newBlockList = JSON.parse(JSON.stringify(blocks));

  newBlockList.map(block => {
    if (block.id === blockId) {
      block.title = title;
    }
  })

  return newBlockList;
}

export const reorderCriterion = (blocks, blockId, sourceIndex, destinationIndex) => {
  let newBlockList = JSON.parse(JSON.stringify(blocks));
  let block = newBlockList.find(block => {
    return block.id === blockId;
  })

  const [removed] = block.criteria.splice(sourceIndex, 1);
  block.criteria.splice(destinationIndex, 0, removed);

  return newBlockList;
}

export const moveCriterion = (blocks, sourceBlockId, destinationBlockId, sourceIndex, destinationIndex) => {
  let newBlockList = JSON.parse(JSON.stringify(blocks));

  // find source block
  let sourceBlock = newBlockList.find(block => {
    return block.id === sourceBlockId;
  })

  // remove from source
  const [removed] = sourceBlock.criteria.splice(sourceIndex, 1);

  // find destination block
  let destinationBlock = newBlockList.find(block => {
    return block.id === destinationBlockId;
  })

  // paste to destination
  destinationBlock.criteria.splice(destinationIndex, 0, removed);

  return newBlockList;
}