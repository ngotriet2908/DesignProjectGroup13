import { combineReducers } from "redux";
import users from "./user/reducers/users";
import navigation from "./navigation/reducers/navigation";
import rubric from "./rubric/reducers/rubric";
import courses from "./courses/reducers/courses";
import grading from "./grading/reducers/grading";

import { connectRouter } from 'connected-react-router'
//
// export default combineReducers({ user });
//

const createRootReducer = (history) => combineReducers({
  router: connectRouter(history),
  users,
  rubric,
  navigation,
  courses,
  grading
})

export default createRootReducer;