import React from 'react';
import "react-loader-spinner/dist/loader/css/react-spinner-loader.css";
import Loader from "react-loader-spinner";

import {URL_PREFIX} from "../services/config";
import {request} from "../services/request";
import {Redirect, Route, Switch} from "react-router-dom";
import SignIn from "./auth/SignIn";
import Home from "./home/Home";
import NotFound from "./error/NotFound";
import CourseRoutes from "./course/CourseRoutes";
import NavigationBar from "./navigation/NavigationBar";
import Footer from "./navigation/Footer";
import styles from "./main.module.css";


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
      <Switch>
        <Route exact path={URL_PREFIX + "/login/"}>
          <SignIn/>
        </Route>
        <Route>
          <div className={styles.container}>
            <NavigationBar/>
            <div className={styles.content}>
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
            <Footer/>
          </div>
        </Route>
      </Switch>
    );
  }
}

export default Main;