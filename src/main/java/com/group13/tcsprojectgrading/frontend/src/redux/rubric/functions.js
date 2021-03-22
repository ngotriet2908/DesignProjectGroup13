import cloneDeep from 'lodash/cloneDeep';
import {isCriterion} from "../../components/rubric/helpers";

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
  } else {
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

export function deleteElement(rubric, id, path) {
  let copy = cloneDeep(rubric);

  let parent = findByPath(copy, path, true)
  console.log(parent);

  // parent.forEach(el => {
  //   if (el.content.id !== id) {
  //
  //   }
  // })

  let index = parent.length - 1;
  while (index >= 0) {
    if (parent[index].content.id === id) {
      parent.splice(index, 1);
    }

    index -= 1;
  }

  // let isRemoved = removeById(copy, id);

  return copy;
}

export function findByPath(rubric, path, returnParent=false) {
  let parts = path.split("/");

  if (path === "") {
    return rubric;
  } else {
    let currentElement = rubric;
    let stop = returnParent ? parts.length - 1 : parts.length;

    for (let i = 1; i < stop; i++) {
      console.log("Part:");
      console.log(parts[i]);
      console.log(currentElement);

      currentElement = currentElement[parts[i]];
    }

    return currentElement;
  }
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

export function createAssessment(rubric) {
  let grades = {}
  flattenCriteria(rubric, grades);
  return grades;
}

export function flattenCriteria(rubric, results) {
  return _flattenCriteria(rubric.children, results)
}

export function _flattenCriteria(where, results) {
  if(where instanceof Array) {
    for(let i = 0; i < where.length; i++) {
      _flattenCriteria(where[i], results);
    }
  }
  else {
    if (isCriterion(where.content.type)) {
      results[where.content.id] = {
        grade: 0,
        comment: "",
      };
    }

    if (where.hasOwnProperty('children')) {
      _flattenCriteria(where.children, results);
    }
  }
}








