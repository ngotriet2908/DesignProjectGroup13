import {applyMiddleware, compose, createStore} from 'redux'
import rootReducer from "./reducers";
import {createBrowserHistory} from 'history'
import {routerMiddleware} from 'connected-react-router'

// export default createStore(
//   rootReducer,
//   window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__()
// );

export const history = createBrowserHistory()

const store = createStore(
  rootReducer(history), // including router state
  compose(
    applyMiddleware(
      routerMiddleware(history), // allows dispatching of history actions
    ),
    window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__()
  ),
);

export default store;