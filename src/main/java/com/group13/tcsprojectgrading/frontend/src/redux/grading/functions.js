import cloneDeep from 'lodash/cloneDeep';
//
// export function alterGrade(oldGrades, criterionId, newGrade) {
//   let newGrades = cloneDeep(oldGrades);
//
//   let index = -1;
//
//   newGrades.forEach((element, i) => {
//     if (element.criterionId === criterionId) {
//       index = i;
//     }
//   })
//
//   console.log(index);
//
//   if (index !== -1) {
//     newGrades[index] = newGrade
//   } else {
//     newGrades.push(newGrade);
//   }
//
//   return newGrades;
// }

export function alterTempGrade(oldGrades, criterionId, newGrade) {
  let newGrades = cloneDeep(oldGrades);
  newGrades[criterionId] = newGrade;
  return newGrades;

  // let index = -1;
  //
  // newGrades.forEach((element, i) => {
  //   if (element.criterionId === criterionId) {
  //     index = i;
  //   }
  // })
  //
  // console.log(index);
  //
  // if (index !== -1) {
  //   newGrades[index] = newGrade
  // } else {
  //   newGrades.push(newGrade);
  // }
}

export function alterGrade(oldGrades, criterionId, newGrade) {
  let newGrades = cloneDeep(oldGrades);

  if (newGrades[criterionId]) {
    newGrades[criterionId].push(
      newGrade
      // {
      //   [userId]: newGrade
      // }
    )
  } else {
    console.log("here")
    newGrades[criterionId] = [
      newGrade
      // {
      //   [userId]: newGrade
      // }
    ]
  }

  return newGrades;
}


// export function alterGrade(oldGrades, criterionId, userId, newGrade) {
//   let newGrades = cloneDeep(oldGrades);
//
//   if (newGrades[criterionId]) {
//     newGrades[criterionId].push(
//       {
//         [userId]: newGrade
//       }
//     )
//   } else {
//     console.log("here")
//     newGrades[criterionId] = [
//       {
//         [userId]: newGrade
//       }
//     ]
//   }
//
//   return newGrades;
// }

export function findCriterion(assessment, criterionId) {
  return assessment[criterionId];
}

// export function findCriterion(assessment, criterionId) {
//   return assessment.find(element => {
//     return element.criterionId === criterionId;
//   })
// }