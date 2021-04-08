export function isGraded(grades, id) {
  return grades.hasOwnProperty(id)
}

export function getGrade(grades, id) {
  if (!grades.hasOwnProperty(id)) {
    return null;
  } else {
    return grades[id].find(grade => {
      return grade.active === true;
    })
  }
}