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
import {Spinner} from "react-bootstrap";

import globalStyles from './helpers/global.module.css';


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
      .then(response => {
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
        <div className={globalStyles.container}>
          <Spinner className={globalStyles.spinner} animation="border" role="status">
            <span className="sr-only">Loading...</span>
          </Spinner>
        </div>
      )
    }

    return (
      <Switch>
        <Route exact path={URL_PREFIX + "/login/"}>
          <SignIn/>
        </Route>
        <Route>
          <div className={styles.container}>
            <Sidebar/>
            <div className={styles.contentContainer}>
              <Switch>
                <Route path={URL_PREFIX + "/courses/:courseId"} component={CourseRoutes}/>
                <Route exact path={URL_PREFIX + "/"}>
                  <Home/>
                </Route>
                <Route>
                  <Redirect to={URL_PREFIX + "/notfound/"}/>
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

export default Main;