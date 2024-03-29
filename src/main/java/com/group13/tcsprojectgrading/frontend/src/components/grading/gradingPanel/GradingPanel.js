import React, { Component } from 'react'
import styles from '../grading.module.css'
import GradeEditor from "./GradeEditor";
import GradeViewer from "./GradeViewer";
import {Can} from "../../permissions/ProjectAbility";
import { subject } from '@casl/ability';


class GradingPanel extends Component {
  constructor (props) {
    super(props);
  }


  render () {
    return (
      <div className={styles.gradingPanelContainer}>
        <Can I="edit" this={subject('Submission', (this.props.submission.grader === null)? {id: -1}:this.props.submission.grader)}>
          <GradeEditor match={this.props.match}/>
        </Can>
        <GradeViewer submission={this.props.submission} match={this.props.match}/>
      </div>
    )
  }
}

export default GradingPanel;
