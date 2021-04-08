import React from 'react';
import { ConnectedRouter } from 'connected-react-router'

import {Provider} from "react-redux";
import { history, store, persistor } from './src/redux/store'
import Main from "./src/components/Main";
import "!style-loader!css-loader!react-toastify/dist/ReactToastify.css"
import {ToastContainer} from 'react-toastify'
import { PersistGate } from 'redux-persist/integration/react'


class App extends React.Component {
  render() {
    return (
      <React.StrictMode>
        <Provider store={store}>
          <PersistGate loading={null} persistor={persistor}>
            <ConnectedRouter history={history}>
              <Main/>
              <ToastContainer/>
            </ConnectedRouter>
          </PersistGate>
        </Provider>
      </React.StrictMode>
    );
  }
}

export default App;