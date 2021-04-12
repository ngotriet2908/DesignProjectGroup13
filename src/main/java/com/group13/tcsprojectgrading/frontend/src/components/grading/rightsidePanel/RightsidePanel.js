import React, { Component } from 'react'
import styles from '../grading.module.css'
import IssuesViewer from "./IssuesViewer";
import FinalGrade from "./FinalGrade";


class RightsidePanel extends Component {
  constructor (props) {
    super(props);
  }

  render () {
    return (
      <div className={styles.rightsidePanel}>
        <IssuesViewer user={this.props.user} submission={this.props.submission} routeParams={this.props.routeParams}/>
        {/*<FinalGrade match={this.props.routeParams}/>*/}
      </div>
    )
  }
}

export default RightsidePanel;
