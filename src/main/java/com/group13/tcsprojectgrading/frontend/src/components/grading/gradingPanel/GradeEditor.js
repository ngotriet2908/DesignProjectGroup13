import React, { Component } from 'react'
import styles from '../grading.module.css'
import Card from "react-bootstrap/Card";
import {connect} from "react-redux";
import {findById} from "../../../redux/rubric/functions";
import {isCriterion as isCriterionChecker} from "../../rubric/helpers";
import Button from "react-bootstrap/Button";
import {alterTempAssessmentGrade, saveGrade} from "../../../redux/grading/actions";
import {findCriterion} from "../../../redux/grading/functions";
import {request} from "../../../services/request";
import {IoPencilOutline, IoCloseOutline, IoThumbsUpOutline, IoListOutline} from "react-icons/io5";
import classnames from 'classnames';
import globalStyles from "../../helpers/global.module.css";
import ButtonTooltip from "../../helpers/ButtonTooltip";


class GradeEditor extends Component {
  constructor (props) {
    super(props)

    this.state = {
      open: false
    }
  }

  onChangeComment = (event) => {
    let newGrade = {
      grade: this.props.criterion.grade,
      comment: event.target.value
    }

    this.props.alterTempAssessmentGrade(this.props.selectedElement, newGrade);
  }

  onChangeGrade = (event) => {
    let newGrade = {
      grade: event.target.value,
      comment: this.props.criterion.comment
    }

    this.props.alterTempAssessmentGrade(this.props.selectedElement, newGrade);
  }

  handleSaveGrade = () => {
    let newGrade = {
      grade: this.props.criterion.grade === "" ? 0 : this.props.criterion.grade,
      description: this.props.criterion.comment === "" ? null : this.props.criterion.comment,
      criterionId: this.props.selectedElement
    }

    let empty = {
      grade: 0,
      comment: ""
    }

    // send request
    request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}/assessments/${this.props.match.params.assessmentId}/grades`, "POST",
      newGrade
    )
      .then(async (response) => {
        let grade = await response.json();
        this.props.alterTempAssessmentGrade(this.props.selectedElement, empty);
        this.props.saveGrade(grade);
      })
      .catch(error => {
        console.error(error.message)
      });
  }

  resetGrade = () => {
    let empty = {
      grade: 0,
      comment: ""
    }

    this.props.alterTempAssessmentGrade(this.props.selectedElement, empty);
  }

  toggleGradeEditor = () => {
    this.setState(prevState => ({
      open: !prevState.open
    }))
  }

  render () {
    let isCriterion = this.props.element.hasOwnProperty("content") && isCriterionChecker(this.props.element.content.type);
    let isGraded = isCriterion && this.props.grades && this.props.grades.length > 0;

    return (
      <div className={styles.gradeEditorContainer}>
        <Card className={styles.panelCard}>
          <Card.Body className={styles.gradeViewerBody}>
            <div className={classnames(styles.gradingCardTitle, styles.gradingCardTitleWithButton)}>
              <h4>Grade Editor</h4>

              {isCriterion && isGraded && !this.state.open &&
                <ButtonTooltip className={classnames(globalStyles.iconButton, styles.gradingCardTitleButton)}
                               content="Edit Grade" placement="bottom"
                               onClick={this.toggleGradeEditor}>
                  <IoPencilOutline size={26}/>
                </ButtonTooltip>
              }
              {isCriterion && isGraded && this.state.open &&
                <ButtonTooltip className={classnames(globalStyles.iconButton, styles.gradingCardTitleCloseButton)}
                               content="Close" placement="bottom"
                               onClick={this.toggleGradeEditor}>
                  <IoCloseOutline size={26}/>
                </ButtonTooltip>
              }
            </div>

            {isCriterion ?
              (!isGraded || this.state.open ?
                <div className={styles.gradeEditorContentContainer}>
                  <div className={styles.gradeEditorCardItem}>
                    Grade
                    <input type="number" value={this.props.criterion.grade} onChange={this.onChangeGrade}/>
                  </div>
                  <div className={classnames(styles.gradeEditorCardItem, styles.gradeEditorCardItemFill)}>
                    Notes (optional)
                    <textarea placeholder={"Provide explanation for the grade or any additional notes that will be visible to students"} value={this.props.criterion.comment} onChange={this.onChangeComment}/>
                  </div>
                  <div className={styles.gradeEditorCardFooter}>
                    <Button className={styles.gradeEditorCardButton} variant="linkLightGray"
                      onClick={this.resetGrade}>Clear</Button>
                    <Button className={styles.gradeEditorCardButton} variant="lightGreen" onClick={this.handleSaveGrade}>Save</Button>
                  </div>
                </div>
                :
                <div className={styles.gradeEditorGradedContainer}>
                  <IoThumbsUpOutline size={40}/>
                  <h6>
                    Graded! Click on the pencil button to regrade
                  </h6>
                </div>)
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
    user: state.users.self,
    selectedElement: state.rubric.selectedElement,
    rubric: state.rubric.rubric,

    element: findById(state.rubric.rubric, state.rubric.selectedElement),
    criterion: findCriterion(state.grading.tempAssessment, state.rubric.selectedElement),
    grades: findCriterion(state.grading.assessment.grades, state.rubric.selectedElement)
  };
};

const actionCreators = {
  saveGrade,
  alterTempAssessmentGrade
}

export default connect(mapStateToProps, actionCreators)(GradeEditor)
