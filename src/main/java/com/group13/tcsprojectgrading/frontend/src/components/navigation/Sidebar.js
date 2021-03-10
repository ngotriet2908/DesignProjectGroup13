import React from "react";
import {Nav} from "react-bootstrap";
import styles from "./navigation.module.css"
import store from "../../redux/store";
import {push} from "connected-react-router";
import {URL_PREFIX} from "../../services/config";
import {request} from "../../services/request";
import {connect} from "react-redux";
import {removeUser} from "../../redux/user/actions";
import {IoHomeOutline, IoLogOutOutline, IoMenuSharp} from "react-icons/io5";

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
      .then(data => {
        // redirect to login and remove user's info
        store.dispatch(push(URL_PREFIX + "/login"));
        this.props.removeUser();
      })
      .catch(error => {
        console.error(error.message);
        // notify user about error
      });
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

            <div className={styles.sidebarHeaderButton} onClick={this.onClickHideSidebar}>
              {this.state.isHidden ?
                <IoMenuSharp size={26}/>
                :
                <IoMenuSharp size={26}/>
              }
            </div>
          </div>

          <div className={styles.sidebarBody}>
            <div className={`${styles.sidebarBodyItem} ${styles.sidebarBodyItemActive}`} onClick={this.onClickLogo}>
              <div className={styles.sidebarBodyItemLeft}>
                <IoHomeOutline className={styles.sidebarIcon} size={26}/>
              </div>
              {!this.state.isHidden &&
                <span className={styles.sidebarBodyItemRight}>
                  Home
                </span>
              }
            </div>
          </div>
        </div>

        <div className={styles.sidebarFooter}>
          <div className={styles.sidebarFooterInner}>
            <div className={styles.sidebarBodyItem}>
              <div className={styles.sidebarBodyItemLeft}>
                {this.props.user &&
                <img height="40" width="40" src={this.props.user.avatar_url} alt={"profile picture"}/>
                }
              </div>
              {!this.state.isHidden &&
                <div className={styles.sidebarBodyItemRight}>
                  {this.props.user && this.props.user.name}
                  <div className={styles.sideBarFooterSignOutButton} onClick={this.onClickSignOut}>
                    <IoLogOutOutline size={26}/>
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
    user: state.user.user
  };
};

export default connect(mapStateToProps, {removeUser})(Sidebar)