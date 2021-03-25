import React, {Component} from "react";
import styles from "../project/project.module.css";
import {URL_PREFIX} from "../../services/config";

import {connect} from "react-redux";
import {Spinner} from "react-bootstrap";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {deleteRubric, saveRubric} from "../../redux/rubric/actions";

import {setCurrentLocation} from "../../redux/navigation/actions";
import Breadcrumbs from "../helpers/Breadcrumbs";

import globalStyles from '../helpers/global.module.css';
import classnames from "classnames";

class Settings extends Component {
  constructor(props) {
    super(props)
  }

  render () {
    if (!this.state.isLoaded) {
      return (
        <div className={globalStyles.container}>
          <Spinner className={globalStyles.spinner} animation="border" role="status">
            <span className="sr-only">Loading...</span>
          </Spinner>
        </div>
      );
    }

    return (
      <div className={globalStyles.container}>
        <Breadcrumbs>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumbs.Item>
          <Breadcrumbs.Item active>
            Settings
          </Breadcrumbs.Item>
        </Breadcrumbs>

        <div className={globalStyles.titleContainer}>
          <h1>Settings</h1>
        </div>

        <div className={styles.container}>
          <div className={classnames(styles.sectionContainer)}>
            <div className={classnames(styles.sectionTitle, styles.sectionTitleWithButton)}>
              <h3 className={styles.sectionTitleH}>{this.props.title}</h3>

              {this.props.icon &&
              <div onClick={this.props.onClickIcon} className={classnames(styles.iconButton, styles.sectionTitleButton)}>
                {this.props.icon}
              </div>
              }
            </div>
          </div>
        </div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric
  };
};

const actionCreators = {
  saveRubric,
  deleteRubric,
  setCurrentLocation
}

export default connect(mapStateToProps, actionCreators)(Settings)