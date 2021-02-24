import { combineReducers } from "redux";
import user from "./user";
import { connectRouter } from 'connected-react-router'
//
// export default combineReducers({ user });
//

const createRootReducer = (history) => combineReducers({
    router: connectRouter(history),
    user
})

export default createRootReducer;