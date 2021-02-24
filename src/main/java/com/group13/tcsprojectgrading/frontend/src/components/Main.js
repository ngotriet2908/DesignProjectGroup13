import React from 'react';
import { Route, Switch, Redirect } from 'react-router-dom'
import Home from "./home/Home";
import SignIn from "./auth/SignIn";
import NotFound from "./error/NotFound";
import "react-loader-spinner/dist/loader/css/react-spinner-loader.css";
import Loader from "react-loader-spinner";

import { connect } from "react-redux";
import {setAuthState} from "../redux/actions";
import {URL_PREFIX} from "../services/config";
import {request} from "../services/request";

class Main extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      loaded: false,
    }
  }

  componentDidMount() {
    this.signIn();
  }

  signIn = () => {
    request("/api/auth/session")
      .then(response => {
        // this.props.setAuthState(true);
        this.setState({
          loaded: true,
        })
      })
      .catch(error => {
        console.error(error.message)
        this.setState({
          loaded: true,
        })
      })
  }

  render() {
    if (!this.state.loaded) {
      return (
        <div>
          <Loader
            type="Puff"
            color="#00BFFF"
            height={100}
            width={100}
            // timeout={3000}
          />
        </div>
      )
    }

    return (
      <div>
        <Switch>
          <Route exact path={URL_PREFIX + "/login/"}>
            <SignIn/>
          </Route>
          <Route exact path={URL_PREFIX + "/"}>
            <Home/>
          </Route>
          <Route>
            <Redirect to={URL_PREFIX + "/notfound/"}/>
            <NotFound/>
          </Route>
        </Switch>
      </div>
    );
  }
}

const mapStateToProps = state => {
  return {
    signedIn: state.user.signedIn
  };
};

export default connect(mapStateToProps, { setAuthState })(Main);
