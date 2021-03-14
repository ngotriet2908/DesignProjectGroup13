import React, { Component } from 'react'
import styles from './grading.module.css'
import {connect} from "react-redux";

import globalStyles from '../helpers/global.module.css';
import Card from "react-bootstrap/Card";
import {isCriterion} from "../rubric/helpers";
import Button from "react-bootstrap/Button";
import {findById} from "../../redux/rubric/functions";
import {findCriterion} from "../../redux/grading/functions";


class GradeViewer extends Component {
  constructor (props) {
    super(props)
  }

  componentDidMount() {

  }

  render () {
    let element = findById(this.props.rubric, this.props.selectedElement)
    let grades = findCriterion(this.props.assessment, this.props.selectedElement);

    return (
      <div className={styles.gradeViewerContainer}>
        {(element.content && isCriterion(element.content.type)) &&
        <Card className={styles.gradeViewerCard}>
          <Card.Body>
            {grades ?
              grades.map((grade, index) => {
                return(
                  <div key={index}>
                    <div>
                    Graded by user {grade.user}
                    </div>
                    <div>
                      Grade: {grade.grade}
                    </div>
                    <div>
                      Explanation: {grade.comment}
                    </div>
                  </div>
                )
              })
              :
              <div>
                Not graded
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
    selectedElement: state.rubric.selectedElement,
    rubric: state.rubric.rubric,
    tempAssessment: state.grading.tempAssessment,
    assessment: state.grading.assessment
  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(GradeViewer)
