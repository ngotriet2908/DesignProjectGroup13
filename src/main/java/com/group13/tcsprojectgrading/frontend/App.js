import React from 'react';
import { ConnectedRouter } from 'connected-react-router'

import {Provider} from "react-redux";
import { history, store, persistor } from './src/redux/store'
import Main from "./src/components/Main";

import { PersistGate } from 'redux-persist/integration/react'

class App extends React.Component {
  render() {
    return (
      <React.StrictMode>
        <Provider store={store}>
          <PersistGate loading={null} persistor={persistor}>
            <ConnectedRouter history={history}>
              <Main/>
            </ConnectedRouter>
          </PersistGate>
        </Provider>
      </React.StrictMode>
    );
  }
}

export default App;