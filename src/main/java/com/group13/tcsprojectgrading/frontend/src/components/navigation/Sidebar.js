import React from "react";
import styles from "./navigation.module.css"
import store from "../../redux/store";
import {goBack, push} from "connected-react-router";
import {URL_PREFIX} from "../../services/config";
import {request} from "../../services/request";
import {connect} from "react-redux";
import {removeUserSelf} from "../../redux/user/actions";
import {
  IoHomeOutline,
  IoGrid,
  IoLogOutOutline,
  IoMenuSharp,
  IoBookOutline,
  IoCreateOutline,
  IoReturnDownBackOutline,
  IoPencilSharp,
  IoListOutline,
  IoReaderOutline,
  IoSettingsOutline,
  IoPersonOutline,
  IoSchool, IoChevronBackSharp
} from "react-icons/io5";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import classnames from 'classnames';
import ButtonTooltip from "../helpers/ButtonTooltip";
import {OverlayTrigger} from "react-bootstrap";
import Tooltip from "react-bootstrap/Tooltip";

class Sidebar extends React.Component {
  constructor (props) {
    super(props);

    this.state = {
      isHidden: true,
    }
  }

  onClickHideSidebar = () => {
    this.setState((prevState) => ({
      isHidden: !prevState.isHidden,
    }));
  }

  onClickLogo = () => {
    store.dispatch(push(URL_PREFIX + "/"))
    this.setState({
      isHidden: true,
    })
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

  tabs = () => {
    let result;

    result = []

    //if sidebar expanded, show text and remove tooltips

    if (this.props.location !== LOCATIONS.home) {
      if (this.state.isHidden) {
        result.push(
          <ButtonTooltip key="back" className={styles.sidebarBodyItem} onClick={() => store.dispatch(goBack())}
          content="Back">
            <div className={styles.sidebarBodyItemLeft}>
              <IoReturnDownBackOutline className={styles.sidebarIcon} size={26}/>
            </div>
          </ButtonTooltip>
        );
      } else {
        result.push(
          <div key="back" className={`${styles.sidebarBodyItem}`} onClick={() => store.dispatch(goBack())}>
            <div className={styles.sidebarBodyItemLeft}>
              <IoReturnDownBackOutline className={styles.sidebarIcon} size={26}/>
            </div>
            <span className={styles.sidebarBodyItemRight}>Back</span>
          </div>
        );
      }
    } else {
      if (this.state.isHidden) {
        result.push(
          <ButtonTooltip key="back" className={classnames(styles.sidebarBodyItem, styles.sidebarBodyItemHidden)}
             onClick={() => store.dispatch(goBack())} content="Back">
            <div className={styles.sidebarBodyItemLeft}>
              <IoReturnDownBackOutline className={styles.sidebarIcon} size={26}/>
            </div>
          </ButtonTooltip>
        );
      } else {
        result.push(
          <div key="back" className={classnames(styles.sidebarBodyItem, styles.sidebarBodyItemHidden)}
               onClick={() => store.dispatch(goBack())}>
            <div className={styles.sidebarBodyItemLeft}>
              <IoReturnDownBackOutline className={styles.sidebarIcon} size={26}/>
            </div>
            <span className={styles.sidebarBodyItemRight}>Back</span>
          </div>
        );
      }
    }

    if (this.props.location === LOCATIONS.home) {
      // home
      if (this.state.isHidden) {
        result.push(
          <ButtonTooltip key="home" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`}
               onClick={this.onClickLogo} content="Home">
              <div className={styles.sidebarBodyItemLeft}>
                <IoHomeOutline className={styles.sidebarIcon} size={26}/>
              </div>
          </ButtonTooltip>
        );
      } else {
        result.push(
          <div key="home" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`}
               onClick={this.onClickLogo}>
            <div className={styles.sidebarBodyItemLeft}>
              <IoHomeOutline className={styles.sidebarIcon} size={26}/>
            </div>
            <span className={styles.sidebarBodyItemRight}>Home</span>
          </div>
        );
      }
    } else {
      if (this.state.isHidden) {
        result.push(
          <ButtonTooltip key="home" className={`${styles.sidebarBodyItem}`} onClick={this.onClickLogo} content="Home">
            <div className={styles.sidebarBodyItemLeft}>
              <IoHomeOutline className={styles.sidebarIcon} size={26}/>
            </div>
          </ButtonTooltip>
        );
      } else {
        result.push(
          <div key="home" className={`${styles.sidebarBodyItem}`} onClick={this.onClickLogo}>
            <div className={styles.sidebarBodyItemLeft}>
              <IoHomeOutline className={styles.sidebarIcon} size={26}/>
            </div>
            <span className={styles.sidebarBodyItemRight}>Home</span>
          </div>
        );
      }
    }

    if (this.props.location === LOCATIONS.course) {
      // course
      if (this.state.isHidden) {
        result.push(
          <ButtonTooltip key="course" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`} onClick={() => {
          }} content="Course">
            <div className={styles.sidebarBodyItemLeft}>
                <IoBookOutline className={styles.sidebarIcon} size={26}/>
            </div>
          </ButtonTooltip>
        );
      } else {
        result.push(
          <div key="course" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`} onClick={() => {
          }}>
            <div className={styles.sidebarBodyItemLeft}>
              <IoBookOutline className={styles.sidebarIcon} size={26}/>
            </div>
            <span className={styles.sidebarBodyItemRight}>Course</span>
          </div>
        );
      }
    } else if (this.props.location === LOCATIONS.project) {
      if (this.state.isHidden) {
        // project
        result.push(
          <ButtonTooltip key="project" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`} onClick={() => {
          }} content="Project">
            <div className={styles.sidebarBodyItemLeft}>
                <IoCreateOutline className={styles.sidebarIcon} size={26}/>
            </div>
          </ButtonTooltip>
        );

        result.push(
          <ButtonTooltip key="settings" className={`${styles.sidebarBodyItem}`} content="Settings"
               onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.props.courseId + "/projects/" + this.props.projectId + "/settings"))}
          >
              <div className={styles.sidebarBodyItemLeft}>
                  <IoSettingsOutline className={styles.sidebarIcon} size={26}/>
              </div>
          </ButtonTooltip>
        );
      } else {
        result.push(
          <div key="project" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`} onClick={() => {
          }}>
            <div className={styles.sidebarBodyItemLeft}>
              <IoCreateOutline className={styles.sidebarIcon} size={26}/>
            </div>
            <span className={styles.sidebarBodyItemRight}>Project</span>
          </div>
        );

        result.push(
          <div key="settings" className={`${styles.sidebarBodyItem}`}
               onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.props.courseId + "/projects/" + this.props.projectId + "/settings"))}
          >
            <div className={styles.sidebarBodyItemLeft}>
              <IoSettingsOutline className={styles.sidebarIcon} size={26}/>
            </div>
            <span className={styles.sidebarBodyItemRight}>Settings</span>
          </div>
        );
      }

    } else if (this.props.location === LOCATIONS.rubric) {
      // rubric
      if (this.state.isHidden) {
        result.push(
          <ButtonTooltip key="rubric" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`} onClick={() => {
          }} content="Rubric">
            <div className={styles.sidebarBodyItemLeft}>
                <IoGrid className={styles.sidebarIcon} size={26}/>
            </div>
          </ButtonTooltip>
        );
      } else {
        result.push(
          <div key="rubric" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`} onClick={() => {
          }}>
            <div className={styles.sidebarBodyItemLeft}>
              <IoGrid className={styles.sidebarIcon} size={26}/>
            </div>
            <span className={styles.sidebarBodyItemRight}>Rubric</span>
          </div>
        );
      }
    } else if (this.props.location === LOCATIONS.grading) {
      // grading
      if (this.state.isHidden) {
        result.push(
          <ButtonTooltip key="grading" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`} onClick={() => {
          }} content="Grading">
            <div className={styles.sidebarBodyItemLeft}>
                <IoPencilSharp className={styles.sidebarIcon} size={26}/>
            </div>
          </ButtonTooltip>
        );
      } else {
        result.push(
          <div key="grading" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`} onClick={() => {
          }}>
            <div className={styles.sidebarBodyItemLeft}>
              <IoPencilSharp className={styles.sidebarIcon} size={26}/>
            </div>
            <span className={styles.sidebarBodyItemRight}>Grading</span>
          </div>
        );
      }
    } else if (this.props.location === LOCATIONS.submissions) {
      // submissions
      if (this.state.isHidden) {
        result.push(
          <ButtonTooltip key="submissions" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`}
                onClick={() => {}} content="Submissions">
            <div className={styles.sidebarBodyItemLeft}>
                <IoListOutline className={styles.sidebarIcon} size={26}/>
            </div>
          </ButtonTooltip>
        );
      } else {
        result.push(
          <div key="submissions" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`}
                onClick={() => {
                }}>
            <div className={styles.sidebarBodyItemLeft}>
              <IoListOutline className={styles.sidebarIcon} size={26}/>
            </div>
            <span className={styles.sidebarBodyItemRight}>Submissions</span>
          </div>
        );
      }
    } else if (this.props.location === LOCATIONS.submission) {
      // submission
      if (this.state.isHidden) {
        result.push(
          <ButtonTooltip key="submission" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`}
                onClick={() => {}} content="Submission">
            <div className={styles.sidebarBodyItemLeft}>
                <IoReaderOutline className={styles.sidebarIcon} size={26}/>
            </div>
          </ButtonTooltip>
        );
      } else {
        result.push(
          <div key="submission" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`}
                onClick={() => {
                }}>
            <div className={styles.sidebarBodyItemLeft}>
              <IoReaderOutline className={styles.sidebarIcon} size={26}/>
            </div>
            <span className={styles.sidebarBodyItemRight}>Submission</span>
          </div>
        );
      }
    } else if (this.props.location === LOCATIONS.settings) {
      if (this.state.isHidden) {
        result.push(
          <ButtonTooltip key="settings" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`} content="Settings"
               onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.props.courseId + "/projects/" + this.props.projectId + "/settings"))}
          >
            <div className={styles.sidebarBodyItemLeft}>
                <IoSettingsOutline className={styles.sidebarIcon} size={26}/>
            </div>
          </ButtonTooltip>
        );
      } else {
        result.push(
          <div key="settings" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`}
               onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.props.courseId + "/projects/" + this.props.projectId + "/settings"))}
          >
            <div className={styles.sidebarBodyItemLeft}>
              <IoSettingsOutline className={styles.sidebarIcon} size={26}/>
            </div>
            <span className={styles.sidebarBodyItemRight}>Settings</span>
          </div>
        );
      }
    } else if (this.props.location === LOCATIONS.graders) {
      if (this.state.isHidden) {
        result.push(
          <ButtonTooltip key="graders" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`} content="Graders"
                         onClick={() => {}}>
            <div className={styles.sidebarBodyItemLeft}>
                <IoPersonOutline className={styles.sidebarIcon} size={26}/>
            </div>
          </ButtonTooltip>
        );
      } else {
        result.push(
          <div key="graders" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`} onClick={() => {
          }}>
            <div className={styles.sidebarBodyItemLeft}>
              <IoPersonOutline className={styles.sidebarIcon} size={26}/>
            </div>
            <span className={styles.sidebarBodyItemRight}>Graders</span>
          </div>
        );
      }
    } else if (this.props.location === LOCATIONS.students) {
      if (this.state.isHidden) {
        result.push(
          <ButtonTooltip key="students" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`} content="Students"
                         onClick={() => {}}>
            <div className={styles.sidebarBodyItemLeft}>
              <IoSchool className={styles.sidebarIcon} size={26}/>
            </div>
          </ButtonTooltip>
        );
      } else {
        result.push(
          <div key="students" className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`} onClick={() => {
          }}>
            <div className={styles.sidebarBodyItemLeft}>
              <IoSchool className={styles.sidebarIcon} size={26}/>
            </div>
            <span className={styles.sidebarBodyItemRight}>Students</span>
          </div>
        );
      }
    }

    return result;
  }

  render() {
    let className = `${styles.sidebar}`;
    if (this.state.isHidden) {
      className += ` ${styles.sidebarHidden}`;
    }

    return (
      <div className={className}>

        <div className={styles.sidebarInnerContainer}>
          <div className={styles.sidebarHeader}>
            {!this.state.isHidden &&
            <div className={styles.sidebarHeaderLeft}>
              <img
                src="http://localhost:8080/img/logo.png"
                alt="logo"
                onClick={this.onClickLogo}/>
            </div>
            }

            {/*The tooltip stays once you close the menu and idk how to go around that, not sure if we even need tooltip
            here anyway*/}
              <div className={styles.sidebarHeaderButton} onClick={this.onClickHideSidebar}>
                {this.state.isHidden ?
                  <IoMenuSharp size={26}/>
                  :
                  <IoChevronBackSharp size={26}/>
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
              {!this.state.isHidden &&
                <div className={styles.sidebarBodyItemRight}>
                  {this.props.user && this.props.user.name}
                  <ButtonTooltip className={styles.sideBarFooterSignOutButton} onClick={this.onClickSignOut}
                                 content="Sign Out" placement="top">
                    <IoLogOutOutline size={26}/>
                  </ButtonTooltip>
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
    projectId: state.navigation.project
  };
};

export default connect(mapStateToProps, {removeUser: removeUserSelf})(Sidebar)