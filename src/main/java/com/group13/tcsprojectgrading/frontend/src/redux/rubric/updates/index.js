export const OPERATION = {
  // "uText": 1,
  // "uGrade": 2,
  // "aCriterion": 3,
  // "aSection": 4,
  // "rCriterion": 5,
  // "rSection": 6,
  replace: 1,
  remove: 2,
  add: 3,
}

export const TYPE = {
  criterionText: 1,
  criterionGrade: 2,
}

export function isSimilarUpdate(a, b) {
  return a.op === b.op && a.type === b.type && a.id === b.id;
}