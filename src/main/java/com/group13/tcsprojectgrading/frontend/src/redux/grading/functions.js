import cloneDeep from 'lodash/cloneDeep';

export function alterTempGrade(oldGrades, criterionId, newGrade) {
  let newGrades = cloneDeep(oldGrades);
  newGrades[criterionId] = newGrade;
  return newGrades;
}

export function saveGrade(oldGrades, criterionId, newGrade) {
  let newGrades = cloneDeep(oldGrades);

  if (newGrades[criterionId]) {
    newGrades[criterionId].push(
      newGrade
    )
  } else {
    newGrades[criterionId] = [
      newGrade
    ]
  }

  return newGrades;
}

export function setActive(oldGrades, criterionId, key) {
  let newGrades = cloneDeep(oldGrades);
  newGrades[criterionId].active = key;
  return newGrades;
}

export function findCriterion(grades, criterionId) {
  return grades[criterionId];
}

// export function findGrades(grades, criterionId) {
//   return grades.filter(grade => {
//     return grade.criterionId === criterionId;
//   });
// }










