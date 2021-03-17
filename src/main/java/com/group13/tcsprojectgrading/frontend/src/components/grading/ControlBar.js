import React, { Component } from 'react'
import styles from './grading.module.css'
import {connect} from "react-redux";
import {IoFlagOutline} from "react-icons/io5";


class ControlBar extends Component {
  constructor (props) {
    super(props)
  }

  flagSubmission = () => {
    alert("Flagged.")
  }

  render () {
    return (
      <div className={styles.controlBarContainer}>
        {/*<Card className={styles.controlBarCard}>*/}
        {/*  <Card.Body>*/}
        {/*<div>*/}
        {/*      Controls and Info, Download submission, Tag submission etc.*/}
        {/*</div>*/}
        <div className={styles.controlBarBody}>
          <div>
            <h2>{this.props.data.submission.name}</h2>
          </div>
          <div>
            <div className={styles.controlBarButton} onClick={this.flagSubmission}>
              <IoFlagOutline size={26}/>
            </div>
          </div>
        </div>
        {/*</Card.Body>*/}
        {/*</Card>*/}
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

export default connect(mapStateToProps, actionCreators)(ControlBar)
