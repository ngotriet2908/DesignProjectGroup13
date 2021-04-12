import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";

import globalStyles from '../../helpers/global.module.css';

import classnames from 'classnames';
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";


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
          <CardContent className={styles.gradeViewerBody}>
            <div className={styles.gradingCardTitle}>
              <h4>Total grade</h4>
            </div>

            {this.props.assessment.finalGrade ?
              <div>
                Grade: {this.props.assessment.finalGrade}
              </div>
              :
              <div className={styles.gradeViewerNotGraded}>
                {/*<IoPlayForwardOutline size={40}/>*/}
                <h6>Grading not finished</h6>
              </div>
            }
          </CardContent>
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
