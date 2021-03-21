import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";


class IssuesPanel extends Component {
  constructor (props) {
    super(props);
  }


  render () {
    return (
      <div className={styles.issuesPanelContainer}>
        Put your issues/notifications here. I don't know if it's possible to create replies to issues,
        but if it is, we can probably add a way to click on an issue/notification to see the related "discussion"
        (in that same panel), dunno
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

export default connect(mapStateToProps, actionCreators)(IssuesPanel)
