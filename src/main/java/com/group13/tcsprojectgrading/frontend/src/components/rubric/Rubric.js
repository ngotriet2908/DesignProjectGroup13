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

    //TODO in case of directly load this page or refresh page <=> no ability is found
    if (ability.rules.length === 0) {
      request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId)
        .then(async response => {
          let data = await response.json()
          if (data.grader !== null && data.grader.privileges !== null) {
            updateAbility(ability, data.grader.privileges, data.grader)
            this.setState({
              project: data
            })
          } else {
            console.log("no grader or privileges found")
          }
          // console.log(ability.rules)
        })
    }

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
      <div className={styles.container}>
        <div className={styles.outline}>
          <RubricOutline/>
        </div>

        <div className={styles.editor}>
          {this.props.isEditing ?
            <RubricEditor/>
            :
            <RubricViewer/>
          }
        </div>

        {this.props.isEditing &&
          <RubricBottomBar courseId={this.props.match.params.courseId} projectId={this.props.match.params.projectId}/>
        }
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


















