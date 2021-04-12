import React from 'react';
import { ConnectedRouter } from 'connected-react-router'

import {Provider} from "react-redux";
import { history, store, persistor } from './src/redux/store'
import Main from "./src/components/Main";
import "!style-loader!css-loader!react-toastify/dist/ReactToastify.css"
import {ToastContainer} from 'react-toastify'
import { PersistGate } from 'redux-persist/integration/react'
import { StylesProvider } from '@material-ui/core/styles';
import { ThemeProvider } from '@material-ui/styles';
import {theme} from "./src/components/helpers/theme";


class App extends React.Component {
  render() {
    return (
      <React.StrictMode>
        <Provider store={store}>
          <PersistGate loading={null} persistor={persistor}>
            <ConnectedRouter history={history}>
              <ThemeProvider theme={theme}>
                <StylesProvider injectFirst>
                  <Main/>
                </StylesProvider>
              </ThemeProvider>
              <ToastContainer/>
            </ConnectedRouter>
          </PersistGate>
        </Provider>
      </React.StrictMode>
    );
  }
}

export default App;