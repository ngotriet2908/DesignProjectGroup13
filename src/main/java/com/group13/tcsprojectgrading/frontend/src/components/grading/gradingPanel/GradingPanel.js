import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import GradeEditor from "./GradeEditor";
import GradeViewer from "./GradeViewer";
import Card from "react-bootstrap/Card";


class GradingPanel extends Component {
  constructor (props) {
    super(props);
  }


  render () {
    return (
      <div className={styles.gradingPanelContainer}>
        <GradeEditor match={this.props.match}/>
        <GradeViewer match={this.props.match}/>
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

export default connect(mapStateToProps, actionCreators)(GradingPanel)
