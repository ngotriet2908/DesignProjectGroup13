import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";

import globalStyles from '../../helpers/global.module.css';
import Card from "react-bootstrap/Card";
import {request} from "../../../services/request";

import classnames from 'classnames';
import {IoCheckboxOutline, IoPlayForwardOutline, IoFileTrayOutline, IoSquareOutline} from "react-icons/io5";


class FinalGrade extends Component {
  constructor (props) {
    super(props);
  }

  componentDidMount() {

  }

  render () {
    return(
      <div className={styles.finalGradeContainer}>
        <Card className={styles.panelCard}>
          <Card.Body className={styles.gradeViewerBody}>
            <div className={styles.gradingCardTitle}>
              <h4>Total grade</h4>
            </div>

            {this.props.assessment.finalGrade ?
              <div>
                Grade: {this.props.assessment.finalGrade}
              </div>
              :
              <div className={styles.gradeViewerNotGraded}>
                <IoPlayForwardOutline size={40}/>
                <h6>Grading not finished</h6>
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
    self: state.users.self,
    assessment: state.grading.assessment
  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(FinalGrade)
