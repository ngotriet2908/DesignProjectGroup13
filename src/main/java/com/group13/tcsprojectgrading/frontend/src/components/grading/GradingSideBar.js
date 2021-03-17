import React, { Component } from 'react'
import styles from './grading.module.css'
import RubricViewer from "./RubricViewer";
import GradeEditor from "./GradeEditor";
import {connect} from "react-redux";

import globalStyles from '../helpers/global.module.css';
import GradeViewer from "./GradeViewer";


class GradingSideBar extends Component {
  constructor (props) {
    super(props)
  }

  componentDidMount() {

  }

  render () {
    return (
      <div className={styles.gradingContainer}>
        <RubricViewer/>
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

export default connect(mapStateToProps, actionCreators)(GradingSideBar)
