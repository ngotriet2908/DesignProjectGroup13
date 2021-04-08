import React, { Component } from 'react'
import styles from './grading.module.css'
import ControlBar from "./controlBar/ControlBar";
import {saveRubric, setSelectedElement} from "../../redux/rubric/actions";
import {connect} from "react-redux";
import {Spinner} from "react-bootstrap";

import globalStyles from '../helpers/global.module.css';
import {createAssessment} from "../../redux/rubric/functions";
import {saveAssessment, saveTempAssessment} from "../../redux/grading/actions";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {Can, ability, updateAbility} from "../permissions/ProjectAbility";
import RubricPanel from "./rubricPanel/RubricPanel";
import GradingPanel from "./gradingPanel/GradingPanel";
import RightsidePanel from "./rightsidePanel/RightsidePanel";


class Grading extends Component {
  constructor (props) {
    super(props)

    this.state = {
      data: this.props.location.data,
      isLoaded: false,
      submission: {},
      rubricCriteria: []
    }
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.grading);
    this.fetchGradingData();
  }

  visitTree(node, result) {
    let obj = {
      id: node.content.id,
      type: node.content.type,
      name: (node.content.type === "0")? "B: " + node.content.title : "C: " + node.content.title
    }
    if (node.content.type === "1") result.push(obj)
    if (node.hasOwnProperty("children")) {
      node.children.forEach((node) => {
        this.visitTree(node, result)
      })
    }
  }

  getAllElements(rubric) {
    let result = []
    rubric.children.forEach((node) => {
      this.visitTree(node, result)
    })
    return result
  }


  fetchGradingData = () => {
    Promise.all([
      request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}/assessments/${this.props.match.params.assessmentId}`),
      request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/rubric`),
      request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}`),
      request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}`),
    ])
      .then(async([res1, res2, res3, res4]) => {
        const assessment = await res1.json();
        const rubric = await res2.json();
        const submission = await res3.json();
        const project = await res4.json();
        
        if (project.privileges !== null) {
          updateAbility(ability, project.privileges, this.props.user)
          console.log(ability)
          // console.log(ability.can('view',"AdminToolbar"))
          // console.log(ability.can('read',"Submissions"))
        } else {
          console.log("No privileges found.")
        }
        // let user = submission.grader;
        // if (user !== null && user.privileges !== null) {
        //   updateAbility(ability, user.privileges, user)
        // } else {
        //   console.log("No grader or privileges found.")
        // }

        // initialise (empty) state for input fields
        this.props.saveTempAssessment(createAssessment(rubric));

        // load grading data
        this.props.saveAssessment(assessment);

        // load rubric
        this.props.setSelectedElement(rubric.id);
        this.props.saveRubric(rubric);

        // load submission
        this.setState({
          submission: submission,
          isLoaded: true,
          rubricCriteria: this.getAllElements(rubric)
        });
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
      <>
        <ControlBar
          submission={this.state.submission}
          // flagSubmission={null}
          // addFlag={this.handleAddFlag}
          // removeFlag={this.handleRemoveFlag}
          // createFlagHandler={this.createFlagHandler}
          // removeFlagHandler={this.removeFlagHandler}
        />

        <div className={styles.container}>
          <RubricPanel match={this.props.match} rubricCriteria={this.state.rubricCriteria}/>
          <GradingPanel submission={this.state.submission} match={this.props.match}/>
          <RightsidePanel user={this.props.user} submission={this.state.submission} routeParams={this.props.match.params}/>
        </div>

      </>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
    user: state.users.self
  };
};

const actionCreators = {
  setSelectedElement,
  saveRubric,
  saveTempAssessment,
  saveAssessment,
  setCurrentLocation
}

export default connect(mapStateToProps, actionCreators)(Grading)
