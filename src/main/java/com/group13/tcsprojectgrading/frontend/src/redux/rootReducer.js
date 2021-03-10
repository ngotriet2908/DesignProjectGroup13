import { combineReducers } from "redux";
import user from "./user/reducers/user";
import navigation from "./navigation/reducers/navigation";
import rubricNew from "./rubricNew/reducers/rubric";
import { connectRouter } from 'connected-react-router'
//
// export default combineReducers({ user });
//

const createRootReducer = (history) => combineReducers({
  router: connectRouter(history),
  user,
  // rubric,
  rubricNew,
  navigation
})

export default createRootReducer;