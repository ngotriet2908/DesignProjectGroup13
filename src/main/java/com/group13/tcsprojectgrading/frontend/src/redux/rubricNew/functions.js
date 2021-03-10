// export const findById = (children, id) => {
//   children.forEach(element => {
//     if (element.id === id) {
//       return element;
//     }
//   })
// }
import cloneDeep from 'lodash/cloneDeep';

export function findById(where, id) {
  if (where.id === id) {
    return where;
  } else {
    return _findById(where.children, id)
  }
}

export function _findById(where, id) {
  let result = null;
  if(where instanceof Array) {
    for(let i = 0; i < where.length; i++) {
      result = _findById(where[i], id);
      if (result) {
        break;
      }
    }
  }
  else
  {
    if (where.content.id === id) {
      return where;
    }

    if (where.hasOwnProperty('children')) {
      result = _findById(where.children, id);
    }
  }
  return result;
}

export function removeById(where, id) {
  let result = false;
  if(where instanceof Array) {
    var i = where.length;
    while (i--) {
      if (where[i].content.id === id) {
        where.splice(i, 1);
      } else {
        result = removeById(where[i], id);
        if (result) {
          break;
        }
      }
    }
  }
  else
  {
    // if (where.content.id === id) {
    //   // delete where;
    //   return where;
    // }

    if (where.hasOwnProperty('children')) {
      result = removeById(where.children, id);
    }
  }
  return result;
}

export function addBlock(rubric, id, newBlock) {
  let copy = cloneDeep(rubric);

  let element = findById(copy, id);
  element.children.push(newBlock);

  return copy;
}

export function addCriterion(rubric, id, newCriterion) {
  let copy = cloneDeep(rubric);

  let element = findById(copy, id);
  element.children.push(newCriterion);

  return copy;
}

export function deleteElement(rubric, id) {
  let copy = cloneDeep(rubric);
  let isRemoved = removeById(copy, id);
  return copy;
}

export function deleteAllElements(rubric) {
  let copy = cloneDeep(rubric);
  copy.children = [];
  return copy;
}

export function changeTitle(rubric, id, newTitle) {
  let copy = cloneDeep(rubric);

  let element = findById(copy, id);
  element.content.title = newTitle;

  return copy;
}

export function changeText(rubric, id, newText) {
  let copy = cloneDeep(rubric);

  let element = findById(copy, id);
  element.content.text = newText;

  return copy;
}

export function changeGrade(rubric, id, newGrade) {
  let copy = cloneDeep(rubric);

  let element = findById(copy, id);
  element.content.grade = newGrade;

  return copy;
}








