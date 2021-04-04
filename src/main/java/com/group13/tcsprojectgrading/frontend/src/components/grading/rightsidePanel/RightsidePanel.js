import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import IssuesViewer from "./IssuesViewer";
import FinalGrade from "./FinalGrade";


class RightsidePanel extends Component {
  constructor (props) {
    super(props);
  }

  render () {
    return (
      <div className={styles.rightsidePanel}>
        <IssuesViewer routeParams={this.props.routeParams}/>
        {/*<FinalGrade match={this.props.routeParams}/>*/}
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

export default connect(mapStateToProps, actionCreators)(RightsidePanel)
