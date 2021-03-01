import React from 'react';
import { Route, Switch, Redirect } from 'react-router-dom'
import Home from "./home/Home";
import SignIn from "./auth/SignIn";
import NotFound from "./error/NotFound";
import "react-loader-spinner/dist/loader/css/react-spinner-loader.css";
import Loader from "react-loader-spinner";

import {URL_PREFIX} from "../services/config";
import {request} from "../services/request";
import Rubric from "./rubric/Rubric";
import Project from "./project/Project";
import {Navbar} from "react-bootstrap";
import store from "../redux/store";
import {push} from "connected-react-router";

import styles from './main.module.css'


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

  onClickLogo = () => {
    store.dispatch(push(URL_PREFIX + "/"))
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
        {/*<Navbar bg="light" variant="light">*/}
        {/*  <Navbar.Brand id={styles.logo} onClick={this.onClickLogo}>ProjectGrading</Navbar.Brand>*/}
        {/*  /!*<Nav className="mr-auto">*!/*/}
        {/*  /!*  <Nav.Link href="#home">Home</Nav.Link>*!/*/}
        {/*  /!*</Nav>*!/*/}
        {/*</Navbar>*/}

        <Switch>
          <Route exact path={URL_PREFIX + "/login/"}>
            <SignIn/>
          </Route>
          <Route exact path={URL_PREFIX + "/"}>
            <Home/>
          </Route>
          <Route exact path={URL_PREFIX + "/projects/34/rubric/"}>
            <Rubric/>
          </Route>
          <Route exact path={URL_PREFIX + "/projects/34"}>
            <Project/>
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

// const mapStateToProps = state => {
//   return {
//     signedIn: state.user.signedIn
//   };
// };

// export default connect(mapStateToProps, { setAuthState })(Main);
export default Main;
