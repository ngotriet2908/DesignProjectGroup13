import cloneDeep from 'lodash/cloneDeep';

export function alterTempGrade(oldGrades, criterionId, newGrade) {
  let newGrades = cloneDeep(oldGrades);
  newGrades[criterionId] = newGrade;
  return newGrades;
}

export function saveGrade(oldGrades, criterionId, newGrade) {
  let newGrades = cloneDeep(oldGrades);

  if (newGrades[criterionId]) {
    newGrades[criterionId].forEach(grade => {
      grade.active = false;
    })

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

export function setActive(oldGrades, criterionId, gradeId) {
  let newGrades = cloneDeep(oldGrades);

  newGrades[criterionId].forEach(grade => {
    grade.active = grade.id === gradeId;
  })

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










