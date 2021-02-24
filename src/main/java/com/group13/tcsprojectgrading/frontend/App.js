import React from 'react';
import { ConnectedRouter } from 'connected-react-router'

import {Provider} from "react-redux";
import store, { history } from './src/redux/store'
import Main from "./src/components/Main";

class App extends React.Component {
  render() {
    return (
      <React.StrictMode>
        <Provider store={store}>
          <ConnectedRouter history={history}>
            <Main/>
          </ConnectedRouter>
        </Provider>
      </React.StrictMode>
    );
  }
}

export default App;