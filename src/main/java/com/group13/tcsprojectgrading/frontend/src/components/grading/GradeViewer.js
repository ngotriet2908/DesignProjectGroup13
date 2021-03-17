import React, { Component } from 'react'
import styles from './grading.module.css'
import {connect} from "react-redux";

import globalStyles from '../helpers/global.module.css';
import Card from "react-bootstrap/Card";
import {isCriterion} from "../rubric/helpers";
import Button from "react-bootstrap/Button";
import {findById} from "../../redux/rubric/functions";
import {findCriterion} from "../../redux/grading/functions";
import {setActive} from "../../redux/grading/actions";
import {request} from "../../services/request";

import classnames from 'classnames';
import {IoCheckboxOutline, IoSquareOutline} from "react-icons/io5";


class GradeViewer extends Component {
  constructor (props) {
    super(props)
  }

  componentDidMount() {

  }

  makeActive = (key) => {
    // send request
    request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}/grading/${this.props.selectedElement}/active/${key}`, "PUT")
      .then(() => {
        this.props.setActive(this.props.selectedElement, key)
      })
      .catch(error => {
        console.error(error.message)
      });
  }

  render () {
    let element = findById(this.props.rubric, this.props.selectedElement)
    let grades = findCriterion(this.props.assessment.grades, this.props.selectedElement);

    return (
      <div className={styles.gradeViewerContainer}>
        {(element.content && isCriterion(element.content.type)) && grades &&
        <Card className={styles.gradeViewerCard}>
          <Card.Body>
            <h4>Grading history</h4>
            {
              grades.history.map((grade, index) => {
                return(
                  <div key={index} onClick={() => this.makeActive(index)} className={classnames(styles.gradeViewerRow, (index === grades.active) && styles.gradeViewerRowActive)}>
                    <div className={styles.gradeViewerRowIcon}>
                      {index === grades.active ?
                        <IoCheckboxOutline size={26}/>
                        :
                        <IoSquareOutline size={26}/>
                      }
                    </div>
                    <div className={styles.gradeViewerRowContent}>
                      <div>
                    Graded by {grade.userId} on {new Date(grade.created).toDateString()}
                      </div>
                      <div>
                        Grade {grade.grade}
                        {grade.comment != null &&
                          <span> with a note: {grade.comment}
                            {/*<div className={styles.gradeViewerRowExplanation}>{grade.comment}</div>*/}
                          </span>
                        }
                      </div>
                    </div>
                  </div>
                )
              })
            }
          </Card.Body>
        </Card>
        }
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    selectedElement: state.rubric.selectedElement,
    rubric: state.rubric.rubric,
    tempAssessment: state.grading.tempAssessment,
    assessment: state.grading.assessment
  };
};

const actionCreators = {
  setActive
}

export default connect(mapStateToProps, actionCreators)(GradeViewer)
