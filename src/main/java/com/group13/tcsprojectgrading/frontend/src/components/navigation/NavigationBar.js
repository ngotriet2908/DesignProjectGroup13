import React from "react";
import Navbar from "react-bootstrap/Navbar";
import styles from "./navigation.module.css";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {URL_PREFIX} from "../../services/config";
import Button from "react-bootstrap/Button";

import { FaSignOutAlt } from "react-icons/fa";
import {connect} from "react-redux";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {removeUser, saveUser} from "../../redux/user/actions";

class NavigationBar extends React.Component {
  onClickLogo = () => {
    store.dispatch(push(URL_PREFIX + "/"))
  }

  onClickSignOut = () => {
    request( "/logout", "POST", {})
      .then(data => {
        console.log(data);
        // redirect to login and remove user's info
        this.props.removeUser();
        store.dispatch(push(URL_PREFIX + "/login"));
      })
      .catch(error => {
        console.error(error.message);
        // notify user about error
      });
  }

  render() {
    return (
      <Navbar id={styles.navBar}>
        <Navbar.Brand id={styles.logo} onClick={this.onClickLogo}>
          <h3>ProGrader</h3>
          {/*<img*/}
          {/*  src="/img/logo.png"*/}
          {/*  width="85"*/}
          {/*  height="60"*/}
          {/*  alt="Logo"*/}
          {/*/>*/}
        </Navbar.Brand>
        <div id={styles.navBarUserInfoContainer}>
          <div >
            {this.props.user != null && this.props.user.name}
          </div>
          <Button variant="primary" onClick={this.onClickSignOut}><FaSignOutAlt size={20}/></Button>
        </div>
      </Navbar>
    );
  }
}

const mapStateToProps = state => {
  return {
    user: state.user.user
  };
};

export default connect(mapStateToProps, {removeUser})(NavigationBar)