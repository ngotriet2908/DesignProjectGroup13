import { combineReducers } from "redux";
import user from "./user/reducers/user";
import rubric from "./rubric/reducers/rubric";
import rubricNew from "./rubricNew/reducers/rubric";
import { connectRouter } from 'connected-react-router'
//
// export default combineReducers({ user });
//

const createRootReducer = (history) => combineReducers({
  router: connectRouter(history),
  user,
  // rubric,
  rubricNew
})

export default createRootReducer;