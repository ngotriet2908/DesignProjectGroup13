import React, { Component } from 'react'
import {connect} from "react-redux";
import {alterGrade} from "../../redux/rubricNew/actions";
import styles from "./rubric.module.css";

class RubricViewerElementGrade extends Component {
  constructor (props) {
    super(props);
  }

  render () {
    return (
      <div className={styles.viewerSectionContainer}>
        <div className={styles.viewerSectionTitle}>
          <h4>Grade</h4>
        </div>
        <div>
          {this.props.data.min} - {this.props.data.max} with {this.props.data.step}-point increments
        </div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {

  };
};

const actionCreators = {
  alterGrade
}

export default connect(mapStateToProps, actionCreators)(RubricViewerElementGrade)