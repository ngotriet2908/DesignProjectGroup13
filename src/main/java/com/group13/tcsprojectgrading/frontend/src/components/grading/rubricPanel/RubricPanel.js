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

  toggleOutlineHidden = () => {
    this.setState(prevState => ({
      isOutlineHidden: !prevState.isOutlineHidden
    }))
  }

  render () {
    return (
      <div className={styles.rubricPanelContainer}>
        <Card className={styles.panelCard}>
          <Card.Body className={styles.gradeViewerBody}>
            <div className={styles.gradingCardTitle}>
              <h4>Rubric</h4>
            </div>
            <div className={styles.rubricPanelInnerContainer}>
              {/*{!this.state.isOutlineHidden ?*/}
              <RubricOutline outlineHidden={this.state.isOutlineHidden} toggleOutlineHidden={this.toggleOutlineHidden}/>
              {/*:*/}
              <RubricViewer outlineHidden={this.state.isOutlineHidden} toggleOutlineHidden={this.toggleOutlineHidden}/>
              {/*}*/}
            </div>
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
