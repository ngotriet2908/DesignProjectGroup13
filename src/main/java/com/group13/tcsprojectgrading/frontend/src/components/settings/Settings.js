import React, {Component} from "react";
import styles from "./settings.module.css";
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
import Card from "react-bootstrap/Card";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {request} from "../../services/request";

class Settings extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoaded: false,
      settings: {}
    }
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.settings);

    request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/settings`)
      .then(async(response) => {
        let settings = await response.json();

        this.setState({
          isLoaded: true,
          settings: settings
        });

      })
      .catch(error => {
        console.error(error.message);
      });
  }

  toggleNotificationsEnabled = () => {
    let notificationsEnabled = !this.state.settings.notificationsEnabled;

    request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/settings`,
      "PUT", {...this.state.settings, notificationsEnabled: notificationsEnabled})
      .then(async(response) => {
        if (response.status === 200) {
          this.setState(prevState => ({
            settings: {
              ...prevState.settings,
              notificationsEnabled: notificationsEnabled
            }
          }))
        } else {
          // TODO: show error
          console.error("Could not update user settings.")
        }
      })
      .catch(error => {
        console.error(error.message);
      });
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
          <div className={classnames(globalStyles.sectionContainer)}>
            <div className={classnames(globalStyles.sectionTitle, styles.sectionTitleWithButton)}>
              <h3 className={globalStyles.sectionTitleH}>Notifications</h3>
            </div>

            <div className={classnames(styles.sectionBody)}>
              <Card>
                <Card.Body>
                  <h5>Enable notifications</h5>

                  <label className={styles.switch}>
                    <input
                      type="checkbox"
                      checked={!!this.state.settings.notificationsEnabled}
                      onChange={this.toggleNotificationsEnabled}
                    />
                    <span className={styles.slider}/>
                  </label>
                </Card.Body>
              </Card>
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