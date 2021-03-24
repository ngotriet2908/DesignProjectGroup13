import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";

import RubricOutline from "./RubricOutline";
import RubricEditor from "./RubricEditor";
import RubricBottomBar from "./RubricBottomBar";

import {request} from "../../services/request";
import {BASE } from "../../services/endpoints";

import {resetUpdates, saveRubric, saveRubricTemp, setCurrentPath, setSelectedElement} from "../../redux/rubric/actions";
import {Spinner} from "react-bootstrap";
import RubricViewer from "./RubricViewer";

import globalStyles from '../helpers/global.module.css';
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {setCurrentLocation} from "../../redux/navigation/actions";

import {Can, ability, updateAbility} from "../permissions/ProjectAbility";
import classnames from 'classnames';

class Rubric extends Component {
  constructor (props) {
    super(props);

    this.state = {
      isLoaded: false,
      project: {}
    }
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.rubric);
    this.props.resetUpdates();
    this.props.setCurrentPath("");

    // if no ability is found
    if (ability.rules.length === 0) {
      Promise.all([
        request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId),
        request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric")
      ])
        .then(async([res1, res2]) => {
          const permissions = await res1.json();
          const rubric = await res2.json();

          // get permissions
          if (permissions.grader !== null && permissions.grader.privileges !== null) {
            updateAbility(ability, permissions.grader.privileges, permissions.grader)
            this.setState({
              project: permissions
            })
          } else {
            console.log("No grader or privileges found.")
          }

          // get rubric
          this.props.saveRubric(rubric);
          this.props.setSelectedElement(rubric.id);

          this.setState({
            isLoaded: true
          });
        })
        .catch(error => {
          console.error(error.message);
          this.setState({
            isLoaded: true
          });
        });
    } else {
      request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric")
        .then(response => {
          return response.json();
        })
        .then(data => {
          this.props.saveRubric(data);
          this.props.setSelectedElement(data.id);

          this.setState({
            isLoaded: true
          });
        })
        .catch(error => {
          console.error(error.message)

          this.setState({
            isLoaded: true
          });
        });
    }
  }

  downloadRubric = () => {
    console.log("download")
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/downloadRubric`, "GET", 'application/pdf')
      .then(response => {
        if (response.status === 200) {
          return response.blob()
        }
      })
      .then((blob) => {
        console.log(blob)
        const file = new Blob([blob], {
          type: 'application/pdf',
        });
        saveAs(file, 'rubric.pdf');
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render () {
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
      <div className={classnames(styles.container)}>
        <div className={styles.outline}>
          <RubricOutline downloadRubric={this.downloadRubric} courseId={this.props.match.params.courseId} projectId={this.props.match.params.projectId}/>
        </div>

        <div className={styles.editor}>
          {this.props.isEditing ?
            <RubricEditor/>
            :
            <RubricViewer downloadRubric={this.downloadRubric}/>
          }
        </div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
    isEditing: state.rubric.isEditing,
  };
};

const actionCreators = {
  saveRubric,
  saveRubricTemp,
  setSelectedElement,
  setCurrentLocation,
  resetUpdates,
  setCurrentPath
}

export default connect(mapStateToProps, actionCreators)(Rubric)


















