import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";

import Card from "react-bootstrap/Card";
import {isCriterion as isCriterionChecker} from "../../rubric/helpers";
import {findById} from "../../../redux/rubric/functions";
import {findCriterion} from "../../../redux/grading/functions";
import {setActive} from "../../../redux/grading/actions";
import {request} from "../../../services/request";

import classnames from 'classnames';
import {IoCheckboxOutline, IoListOutline, IoFileTrayOutline, IoSquareOutline} from "react-icons/io5";
import {findUserById} from "../../../redux/user/functions";
import {ability, Can} from "../../permissions/ProjectAbility";
import { subject } from '@casl/ability';


class GradeViewer extends Component {
  constructor (props) {
    super(props);

    this.state = {
      isLoaded: false
    }
  }

  makeActive = (id) => {
    if (!ability.can('edit', subject('Submission', (this.props.submission.grader === null)? {id: -1}:this.props.submission.grader))) return

      request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}/assessments/${this.props.match.params.assessmentId}/grades/${id}/activate`, "POST")
      .then(() => {
        this.props.setActive(this.props.selectedElement, id)
      })
      .catch(error => {
        console.error(error.message)
      });
  }

  render () {
    let isCriterion = this.props.element.hasOwnProperty("content") && isCriterionChecker(this.props.element.content.type);
    let isGraded = this.props.grades;

    return (
      <div className={styles.gradeViewerContainer}>
        <Card className={styles.panelCard}>
          <Card.Body className={styles.gradeViewerBody}>
            <div className={styles.gradingCardTitle}>
              <h4>Grading history</h4>
            </div>

            {isCriterion ?
              (isGraded ?
                <div className={styles.gradeViewerBodyScroll}>
                  {this.props.grades.map((grade, index) => {
                    return (
                      <div key={index} onClick={() => this.makeActive(grade.id)}
                        className={classnames(styles.gradeViewerRow, grade.active && styles.gradeViewerRowActive)}>
                        <div className={styles.gradeViewerRowIcon}>
                          {grade.active ?
                            <IoCheckboxOutline size={26}/>
                            :
                            <IoSquareOutline size={26}/>
                          }
                        </div>
                        <div className={styles.gradeViewerRowContent}>
                          <div>
                              Graded by {grade.grader.name} on {new Date(grade.gradedAt).toDateString()}
                          </div>
                          <div>
                              Grade {grade.grade}
                            {grade.description != null &&
                              <span> with a note: {grade.description}
                              </span>
                            }
                          </div>
                        </div>
                      </div>
                    )
                  })
                  }
                </div>
                :
                <div className={styles.gradeViewerNotGraded}>
                  <IoFileTrayOutline size={40}/>
                  <h6>Not graded yet.</h6>
                </div>
              )
              :
              <div className={styles.gradeViewerNotGraded}>
                <IoListOutline size={40}/>
                <h6>Choose a criterion</h6>
              </div>
            }
          </Card.Body>
        </Card>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    selectedElement: state.rubric.selectedElement,
    rubric: state.rubric.rubric,
    users: state.users.users,
    self: state.users.self,

    element: findById(state.rubric.rubric, state.rubric.selectedElement),
    grades: findCriterion(state.grading.assessment.grades, state.rubric.selectedElement)
  };
};

const actionCreators = {
  setActive
}

export default connect(mapStateToProps, actionCreators)(GradeViewer)
