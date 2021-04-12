import React from "react";
import styles from "./navigation.module.css"
import store from "../../redux/store";
import {goBack, push} from "connected-react-router";
import {URL_PREFIX} from "../../services/config";
import {request} from "../../services/request";
import {connect} from "react-redux";
import {removeUserSelf} from "../../redux/user/actions";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import classnames from 'classnames';
import {HOST} from "../../services/endpoints";
import {toggleSidebarHidden} from "../../redux/navigation/actions";

import MenuIcon from '@material-ui/icons/Menu';
import MenuOpenIcon from '@material-ui/icons/MenuOpen';
import HomeIcon from '@material-ui/icons/Home';
import KeyboardBackspaceIcon from '@material-ui/icons/KeyboardBackspace';
import ImportContactsIcon from '@material-ui/icons/ImportContacts';
import AssignmentIcon from '@material-ui/icons/Assignment';
import SettingsIcon from '@material-ui/icons/Settings';
import ListIcon from '@material-ui/icons/List';
import GradeIcon from '@material-ui/icons/Grade';
import AllInboxIcon from '@material-ui/icons/AllInbox';
import ListAltIcon from '@material-ui/icons/ListAlt';
import PeopleIcon from '@material-ui/icons/People';
import CreateIcon from '@material-ui/icons/Create';
import ExitToAppIcon from '@material-ui/icons/ExitToApp';
import withTheme from "@material-ui/core/styles/withTheme";


class Sidebar extends React.Component {
  constructor (props) {
    super(props);
  }

  onClickHideSidebar = () => {
    this.props.toggleSidebarHidden();
  }

  onClickLogo = () => {
    store.dispatch(push(URL_PREFIX + "/"))
  }

  onClickSignOut = () => {
    request( "/logout", "POST", {})
      .then(() => {
        // redirect to login and remove user's info
        store.dispatch(push(URL_PREFIX + "/login"));
        this.props.removeUser();
      })
      .catch(error => {
        console.error(error.message);
        // notify user about error
      });
  }

  createTab = (key, onClick, Icon, classNames) => {
    // let isActive = classNames.includes(styles.sidebarBodyItemActive)

    return(
      <div
        key={key}
        className={classnames(styles.sidebarBodyItem, ...classNames)}
        onClick={onClick}
        // style={isActive ? {
        //   backgroundColor: this.props.theme.palette.primary.main,
        //   color: "white"
        // } : {}}
      >
        <div className={styles.sidebarBodyItemLeft}>
          <Icon className={styles.sidebarIcon} />
        </div>
        {!this.props.sidebarHidden &&
          <span className={styles.sidebarBodyItemRight}>
            {key}
          </span>
        }
      </div>
    )
  }

  tabs = () => {
    let result = [];

    if (this.props.location !== LOCATIONS.home) {
      result.push(
        this.createTab("Back", () => store.dispatch(goBack()), KeyboardBackspaceIcon, [])
      )
    } else {
      result.push(
        this.createTab("Back", () => store.dispatch(goBack()), KeyboardBackspaceIcon, [styles.sidebarBodyItemHidden])
      )
    }

    if  (this.props.location === LOCATIONS.home) {
      // home
      result.push(
        this.createTab("Home", () => this.onClickLogo(), HomeIcon, [styles.sidebarBodyItemActive])
      )
    } else {
      result.push(
        this.createTab("Home", () => this.onClickLogo(), HomeIcon, [])
      )
    }

    if (this.props.location === LOCATIONS.course) {
      // course
      result.push(
        this.createTab("Course", () => {}, ImportContactsIcon, [styles.sidebarBodyItemActive])
      )
    } else if (this.props.location === LOCATIONS.project) {
      // project
      result.push(
        this.createTab("Project", () => {}, AssignmentIcon, [styles.sidebarBodyItemActive])
      )

      result.push(
        this.createTab("Settings",
          () => store.dispatch(push(URL_PREFIX + "/courses/" + this.props.courseId + "/projects/" + this.props.projectId + "/settings")),
          SettingsIcon, [])
      )
    } else if (this.props.location === LOCATIONS.rubric) {
      // rubric
      result.push(
        this.createTab("Rubric", () => {}, ListIcon, [styles.sidebarBodyItemActive])
      )
    } else if (this.props.location === LOCATIONS.grading) {
      // grading
      result.push(
        this.createTab("Grading", () => {}, GradeIcon, [styles.sidebarBodyItemActive])
      )
    } else if (this.props.location === LOCATIONS.submissions) {
      // submissions
      result.push(
        this.createTab("Submissions", () => {}, AllInboxIcon, [styles.sidebarBodyItemActive])
      )
    } else if (this.props.location === LOCATIONS.submission) {
      // submission
      result.push(
        this.createTab("Submission", () => {}, ListAltIcon, [styles.sidebarBodyItemActive])
      )
    } else if (this.props.location === LOCATIONS.settings) {

      result.push(
        this.createTab("Settings",
          () => store.dispatch(push(URL_PREFIX + "/courses/" + this.props.courseId + "/projects/" + this.props.projectId + "/settings")),
          SettingsIcon, [styles.sidebarBodyItemActive])
      )
    } else if (this.props.location === LOCATIONS.graders) {
      result.push(
        this.createTab("Graders", () => {}, CreateIcon, [styles.sidebarBodyItemActive])
      )
    } else if (this.props.location === LOCATIONS.students) {
      result.push(
        this.createTab("Students", () => {}, PeopleIcon, [styles.sidebarBodyItemActive])
      )
    }

    return result;
  }

  render() {
    return (
      <div className={classnames(styles.sidebar, this.props.sidebarHidden ? styles.sidebarHidden : styles.sidebarShown)}>
        <div className={styles.sidebarInnerContainer}>
          <div className={styles.sidebarHeader}>
            {!this.props.sidebarHidden &&
            <div className={styles.sidebarHeaderLeft}>
              <img
                src={HOST + "/img/logo.png"}
                alt="logo"
                onClick={this.onClickLogo}
              />
            </div>
            }

            <div className={styles.sidebarHeaderButton} onClick={this.onClickHideSidebar}>
              {!this.props.sidebarHidden ?
                <MenuOpenIcon size={32}/>
                :
                <MenuIcon size={32}/>
              }
            </div>
          </div>

          <div className={styles.sidebarBody}>
            {this.tabs()}
          </div>
        </div>

        <div className={styles.sidebarFooter}>
          <div className={styles.sidebarFooterInner}>
            <div className={styles.sidebarBodyItem}>
              <div className={styles.sidebarBodyItemLeft}>
                {this.props.user &&
                <img height="40" width="40" src={this.props.user.avatar} alt={"profile picture"}/>
                }
              </div>
              {!this.props.sidebarHidden &&
                <div className={styles.sidebarBodyItemRight}>
                  <span>
                    {this.props.user && this.props.user.name}
                  </span>
                  <div className={styles.sideBarFooterSignOutButton} onClick={this.onClickSignOut}>
                    <ExitToAppIcon/>
                  </div>
                </div>
              }
            </div>
          </div>
        </div>
      </div>
    );
  }
}


const mapStateToProps = state => {
  return {
    user: state.users.self,
    location: state.navigation.location,
    courseId: state.navigation.course,
    projectId: state.navigation.project,
    sidebarHidden: state.navigation.sidebarHidden,
  };
};

const actionCreators = {
  removeUser: removeUserSelf,
  toggleSidebarHidden
}

export default connect(mapStateToProps, actionCreators)(withTheme(Sidebar))