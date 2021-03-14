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
import {saveTempAssessment} from "../../redux/grading/actions";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";


class Grading extends Component {
  constructor (props) {
    super(props)

    this.rubric = rubric;

    this.state = {
      isLoaded: false,
    }

    // TODO: if submission is undefined, reload it from the server
    console.log(this.props.location.submission);
  }

  componentDidMount() {
    this.props.setSelectedElement(this.rubric.id);
    this.props.saveRubric(this.rubric);

    // this.getCurrentAssessment();

    this.createGradingSheet();

    this.setState({
      isLoaded: true
    });


  }

  getCurrentAssessment = () => {
    request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}/grading`)
      .then(async(response) => {

        if (response.status !== 404) {
          let assessment = await response.json();
          console.log(assessment);
        }
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  createGradingSheet = () => {
    let assessment = createAssessment(this.rubric);
    this.props.saveTempAssessment(assessment);
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
        <ControlBar/>
        <div className={styles.container}>
          <FileViewer/>
          <SideBar/>
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
  saveTempAssessment
}

export default connect(mapStateToProps, actionCreators)(Grading)
