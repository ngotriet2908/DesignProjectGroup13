import cloneDeep from 'lodash/cloneDeep';

export function removeUser(oldUsers, userId) {
  return oldUsers.filter(user => {
    return user.id === userId;
  });
}

export function addUser(oldUsers, user) {
  let newUsers = cloneDeep(oldUsers);
  newUsers.push(user);
  return newUsers;
}

export function findUserById(users, userId) {
  return users.find(user => {
    return user.id === userId;
  })
}