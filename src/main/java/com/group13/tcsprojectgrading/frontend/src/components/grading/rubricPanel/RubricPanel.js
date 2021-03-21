import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import RubricOutline from "./RubricOutline";


class RubricPanel extends Component {
  constructor (props) {
    super(props);
  }


  render () {
    return (
      <div className={styles.rubricPanelContainer}>
        <RubricOutline/>
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
