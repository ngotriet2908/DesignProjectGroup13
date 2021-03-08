import {applyMiddleware, compose, createStore} from 'redux'
import createRootReducer from "./rootReducer";
import {createBrowserHistory} from 'history'
import {routerMiddleware} from 'connected-react-router'
import { persistStore, persistReducer } from 'redux-persist'
import storage from 'redux-persist/lib/storage'

const persistConfig = {
  key: 'root',
  storage,
  blacklist: ['history', 'rubricNew']
}

export const history = createBrowserHistory();
const rootReducer = createRootReducer(history);
const persistedReducer = persistReducer(persistConfig, rootReducer)

export const store = createStore(
  persistedReducer, // including router state
  compose(
    applyMiddleware(
      routerMiddleware(history), // allows dispatching of history actions
    ),
    window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__()
  ),
);

export let persistor = persistStore(store)

export default store;