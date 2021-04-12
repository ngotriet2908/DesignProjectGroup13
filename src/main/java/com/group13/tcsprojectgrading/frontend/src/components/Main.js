import React from 'react';

import {URL_PREFIX} from "../services/config";
import {request} from "../services/request";
import {Redirect, Route, Switch} from "react-router-dom";
import SignIn from "./auth/SignIn";
import Home from "./home/Home";
import NotFound from "./error/NotFound";
import CourseRoutes from "./course/CourseRoutes";
import styles from "./main.module.css";
import Sidebar from "./navigation/Sidebar";

import globalStyles from './helpers/global.module.css';
import classnames from 'classnames';
import {connect} from "react-redux";
import CircularProgress from "@material-ui/core/CircularProgress";


class Main extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoaded: false,
    }
  }

  componentDidMount() {
    this.signIn();
  }

  signIn = () => {
    request("/api/auth/session")
      .then(() => {
        // this.props.setAuthState(true);
        this.setState({
          isLoaded: true,
        })
      })
      .catch(error => {
        console.error(error.message)
        this.setState({
          isLoaded: true,
        })
      })
  }

  render() {
    if (!this.state.isLoaded) {
      return(
        <div className={globalStyles.screenContainer}>
          <CircularProgress className={globalStyles.spinner}/>
        </div>
      )
    }

    return (
      <Switch>
        <Route exact path={URL_PREFIX + "/login/"}>
          <SignIn/>
        </Route>
        <Route>
          <div className={globalStyles.appContainer}>
            <Sidebar/>
            <div className={classnames(globalStyles.mainContainer, !this.props.sidebarHidden ? styles.contentContainerShifted : styles.contentContainerStable)}>
              <Switch>
                <Route path={URL_PREFIX + "/courses/:courseId"} component={CourseRoutes}/>

                <Route exact path={URL_PREFIX + "/"}>
                  <Home/>
                </Route>

                <Route>
                  <Redirect to={URL_PREFIX + "/404/"}/>
                  <NotFound/>
                </Route>
              </Switch>
            </div>
          </div>
        </Route>
      </Switch>
    );
  }
}

const mapStateToProps = state => {
  return {
    sidebarHidden: state.navigation.sidebarHidden,
  };
};

export default connect(mapStateToProps, null)(Main)