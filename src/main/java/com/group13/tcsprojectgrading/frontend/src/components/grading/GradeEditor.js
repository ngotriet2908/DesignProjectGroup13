import React, { Component } from 'react'
import styles from './grading.module.css'
import Card from "react-bootstrap/Card";
import {saveRubric, setSelectedElement} from "../../redux/rubric/actions";
import {connect} from "react-redux";
import {findById} from "../../redux/rubric/functions";
import {isCriterion} from "../rubric/helpers";
import Button from "react-bootstrap/Button";
import {alterGrade, alterTempAssessmentGrade} from "../../redux/grading/actions";
import {findCriterion} from "../../redux/grading/functions";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {IoChevronDownSharp, IoPencilOutline, IoCloseOutline, IoCheckboxOutline} from "react-icons/io5";
import classnames from 'classnames';


class GradeEditor extends Component {
  constructor (props) {
    super(props)

    this.state = {
      open: false
    }
  }

  onChangeComment = (event) => {
    let criterion = findCriterion(this.props.tempAssessment, this.props.selectedElement);

    let newGrade = {
      grade: criterion.grade,
      comment: event.target.value
    }

    this.props.alterTempAssessmentGrade(this.props.selectedElement, newGrade);
  }

  onChangeGrade = (event) => {
    let criterion = findCriterion(this.props.tempAssessment, this.props.selectedElement);

    let newGrade = {
      grade: event.target.value,
      comment: criterion.comment
    }

    this.props.alterTempAssessmentGrade(this.props.selectedElement, newGrade);
  }

  saveGrade = () => {
    let criterion = findCriterion(this.props.tempAssessment, this.props.selectedElement);

    let newGrade = {
      userId: this.props.user.id,
      grade: criterion.grade === "" ? 0 : criterion.grade,
      comment: criterion.comment === "" ? null : criterion.comment,
      // isActive: true,
      created: Date.now()
    }

    let empty = {
      grade: 0,
      comment: ""
    }

    // send request
    request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}/grading/${this.props.selectedElement}`, "PUT",
      newGrade
    )
      .then(() => {
        this.props.alterTempAssessmentGrade(this.props.selectedElement, empty);
        this.props.alterGrade(this.props.selectedElement, newGrade);
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
    let element = findById(this.props.rubric, this.props.selectedElement)
    let criterion = findCriterion(this.props.tempAssessment, this.props.selectedElement);

    return (
      <div className={styles.gradeEditorContainer}>
        {(element.content && criterion && isCriterion(element.content.type)) &&
        <Card className={styles.gradeEditorCard}>
          <Card.Body>
            {(!this.props.assessment.grades[this.props.selectedElement]) || this.state.open ?
              <div>
                {this.props.assessment.grades[this.props.selectedElement] &&
                this.props.assessment.grades[this.props.selectedElement].history.length > 0 &&
                this.state.open &&
                  <div className={classnames(styles.gradeEditorGradedClose, this.state.open && styles.gradeEditorGradedExpandOpened)} onClick={this.toggleGradeEditor}>
                    <IoCloseOutline size={24}/>
                  </div>
                }
                <div className={styles.gradeEditorCardItem}>
                  Grade
                  <input type="number" value={criterion.grade} onChange={this.onChangeGrade}/>
                </div>
                <div className={styles.gradeEditorCardItem}>
                  Notes (optional)
                  <input type="text" value={criterion.comment} onChange={this.onChangeComment}/>
                </div>
                <div className={styles.gradeEditorCardFooter}>
                  <Button className={styles.gradeEditorCardButton} variant="linkLightGray"
                    onClick={this.resetGrade}>Reset</Button>
                  <Button className={styles.gradeEditorCardButton} variant="lightGreen" onClick={this.saveGrade}>Save
                    grade</Button>
                </div>
              </div>
              :
              <div className={styles.gradeEditorGradedContainer}>
                <div className={styles.gradeEditorGradedIcon}>
                  <IoCheckboxOutline size={36}/>
                </div>
                <div>
                  Graded! Click on the pencil to show the grading panel if you want to regrade
                  this part.<IoPencilOutline className={classnames(styles.gradeEditorGradedExpand)} onClick={this.toggleGradeEditor} size={20}/>
                </div>
                {/*<div className={classnames(styles.gradeEditorGradedExpand)} onClick={this.toggleGradeEditor}>*/}
                {/*  <IoChevronDownSharp size={24}/>*/}
                {/*</div>*/}
              </div>
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
    user: state.users.self,
    selectedElement: state.rubric.selectedElement,
    rubric: state.rubric.rubric,
    tempAssessment: state.grading.tempAssessment,
    assessment: state.grading.assessment
  };
};

const actionCreators = {
  alterGrade,
  alterTempAssessmentGrade
}

export default connect(mapStateToProps, actionCreators)(GradeEditor)
