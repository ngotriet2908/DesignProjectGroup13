import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import RubricOutline from "./RubricOutline";
import Card from "react-bootstrap/Card";
import RubricViewer from "./RubricViewer";


class RubricPanel extends Component {
  constructor (props) {
    super(props);

    this.state = {
      isOutlineHidden: false,
    }
  }

  toggleOutline = (isOutlineHidden) => {
    this.setState({
      isOutlineHidden: isOutlineHidden
    })
  }

  render () {
    return (
      <div className={styles.rubricPanelContainer}>
        <Card className={styles.panelCard}>
          <Card.Body className={styles.gradeViewerBody}>
            <div className={styles.gradingCardTitle}>
              <h4>Rubric</h4>
            </div>
            {!this.state.isOutlineHidden ?
              <RubricOutline/>
              :
              <RubricViewer/>
            }
          </Card.Body>
        </Card>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {

  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(RubricPanel)
