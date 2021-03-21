import React, { Component } from 'react'
import styles from './grading.module.css'
import FileViewer from "./FileViewer";
import SideBar from "./GradingSideBar";
import ControlBar from "./ControlBar";
import {rubric} from "../rubric/rubricSample";
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


class Grading extends Component {
  constructor (props) {
    super(props)

    this.state = {
      data: this.props.location.data,
      isLoaded: false,
    }

    // TODO: if submission is undefined, reload it from the server
    // console.log(this.props.location.data);
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.grading);
    this.fetchGradingData();
  }

  getCurrentAssessment = () => {
    request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}/grading`)
      .then(async(response) => {

        if (response.status !== 404) {
          let assessment = await response.json();
          // initialise (empty) state for input fields
          this.createGradingSheet();

          // load grading data
          this.props.saveAssessment(assessment);

          // if submission is missing, fetch it
          if (!this.props.location.data) {
            request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}`)
              .then(async(response) => {
                let submission = await response.json();

                this.setState({
                  data: submission,
                  isLoaded: true
                });
              })
          } else {
            this.setState({
              isLoaded: true
            });
          }
        }
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  fetchGradingData = () => {
    // TODO, ignores passed data
    Promise.all([
      request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}/grading`),
      request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}`),
      request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/rubric`)
    ])
      .then(async([res1, res2, res3]) => {
        const assessment = await res1.json();
        const submission = await res2.json();
        const rubric = await res3.json();

        let user = submission.user
        if (user !== null && user.privileges !== null) {
          updateAbility(ability, user.privileges, user)
        } else {
          console.log("no grader or privileges found")
        }
        console.log(ability.rules)

        // console.log(assessment);
        // console.log(submission);
        // console.log(rubric);

        // initialise (empty) state for input fields
        this.props.saveTempAssessment(createAssessment(rubric));

        // load grading data
        this.props.saveAssessment(assessment);

        // load rubric
        this.props.setSelectedElement(rubric.id);
        this.props.saveRubric(rubric);

        // load submission
        this.setState({
          data: submission,
          isLoaded: true,
        });
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  // createGradingSheet = () => {
  //   let assessment = createAssessment(this.rubric);
  //   this.props.saveTempAssessment(assessment);
  // }

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
        <ControlBar data={this.state.data}/>
        <div className={styles.container}>
          <FileViewer/>
          <SideBar data={this.state.data} match={this.props.match}/>
        </div>
      </>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
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
