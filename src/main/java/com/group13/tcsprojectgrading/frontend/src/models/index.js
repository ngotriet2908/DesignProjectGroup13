export function isEmpty(object) {
  for(var i in object) {
    return true;
  }

  return false;
}