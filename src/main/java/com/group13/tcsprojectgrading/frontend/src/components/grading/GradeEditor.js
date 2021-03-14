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


class GradeEditor extends Component {
  constructor (props) {
    super(props)

    // this.state = {
    //   comment: "",
    //   grade: 0,
    // }
  }

  onChangeComment = (event) => {
    let criterion = findCriterion(this.props.tempAssessment, this.props.selectedElement);

    let newGrade = {
      // criterionId: this.props.selectedElement,
      grade: criterion.grade,
      comment: event.target.value
    }

    this.props.alterTempAssessmentGrade(this.props.selectedElement, newGrade);
  }

  onChangeGrade = (event) => {
    let criterion = findCriterion(this.props.tempAssessment, this.props.selectedElement);

    let newGrade = {
      // criterionId: this.props.selectedElement,
      grade: event.target.value,
      comment: criterion.comment
    }

    this.props.alterTempAssessmentGrade(this.props.selectedElement, newGrade);
  }

  saveGrade = () => {
    console.log("Saving grade");
    let criterion = findCriterion(this.props.tempAssessment, this.props.selectedElement);

    let newGrade = {
      user: this.props.user.id,
      grade: criterion.grade,
      comment: criterion.comment
    }

    // send request


    this.props.alterGrade(this.props.selectedElement, newGrade);
  }


  render () {
    let element = findById(this.props.rubric, this.props.selectedElement)
    let criterion = findCriterion(this.props.tempAssessment, this.props.selectedElement);

    return (
      <div className={styles.gradeEditorContainer}>
        {(element.content && isCriterion(element.content.type)) &&
        <Card className={styles.gradeEditorCard}>
          <Card.Body>
            {/*{(element.content && isCriterion(element.content.type)) ?*/}
            <div>
              <div>
                Grade:
                <input type="number" value={criterion.grade} onChange={this.onChangeGrade}/>
              </div>
              <div>
                Comment:
                <input type="text" value={criterion.comment} onChange={this.onChangeComment}/>
              </div>
              <div>
                <Button variant="lightGreen" onClick={this.saveGrade}>Save</Button>
              </div>
            </div>
            {/*  :*/}
            {/*  <div>*/}
            {/*    select criterion*/}
            {/*  </div>*/}
            {/*}*/}
          </Card.Body>
        </Card>
        }
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    user: state.user.user,
    selectedElement: state.rubric.selectedElement,
    rubric: state.rubric.rubric,
    tempAssessment: state.grading.tempAssessment
  };
};

const actionCreators = {
  // setSelectedElement,
  // saveRubric
  alterGrade,
  alterTempAssessmentGrade
}

export default connect(mapStateToProps, actionCreators)(GradeEditor)
