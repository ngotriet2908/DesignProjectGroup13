import React, { Component } from 'react'
import styles from './grading.module.css'
import RubricViewer from "./RubricViewer";
import GradeEditor from "./GradeEditor";
import {connect} from "react-redux";

import globalStyles from '../helpers/global.module.css';
import GradeViewer from "./GradeViewer";
import {Can, ability, updateAbility} from "../permissions/ProjectAbility";
import { subject } from '@casl/ability';

class GradingSideBar extends Component {
  constructor (props) {
    super(props)
  }

  componentDidMount() {
    // console.log(subject("Grading", this.props.data.submission))
    // console.log(ability.can("write", subject("Grading", this.props.data.submission)))
    // console.log(ability.can("write", this.props.data.submission))
    // console.log(ability.can("write", "Rubric"))
  }

  render () {
    return (
      <div className={styles.gradingContainer}>
        <RubricViewer/>
        {/*// TODO*/}
        {/*<Can do="write" on={subject("Grading", this.props.data.submission.grader)}>*/}
        <GradeEditor match={this.props.match}/>
        {/*</Can>*/}
        <Can I="read" a="Grading">
          <GradeViewer match={this.props.match}/>
        </Can>
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
