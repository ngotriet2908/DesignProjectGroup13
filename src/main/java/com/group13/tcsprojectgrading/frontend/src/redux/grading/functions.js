import cloneDeep from 'lodash/cloneDeep';

export function alterTempGrade(oldGrades, criterionId, newGrade) {
  let newGrades = cloneDeep(oldGrades);
  newGrades[criterionId] = newGrade;
  return newGrades;
}

export function alterGrade(oldGrades, criterionId, newGrade) {
  let newGrades = cloneDeep(oldGrades);

  if (newGrades[criterionId]) {
    newGrades[criterionId].history.push(
      newGrade
    )
    newGrades[criterionId].active += 1;
  } else {
    newGrades[criterionId] = {}
    newGrades[criterionId].history = [
      newGrade
    ]

    newGrades[criterionId].active = 0;
  }

  return newGrades;
}

// export function setActive(oldGrades, criterionId, grade) {
//   let newGrades = cloneDeep(oldGrades);
//
//   newGrades[criterionId].forEach((element) => {
//     element.isActive = isEqual(element, grade);
//   })
//
//   return newGrades;
// }

export function setActive(oldGrades, criterionId, key) {
  let newGrades = cloneDeep(oldGrades);
  newGrades[criterionId].active = key;
  return newGrades;
}

export function findCriterion(assessment, criterionId) {
  return assessment[criterionId];
}