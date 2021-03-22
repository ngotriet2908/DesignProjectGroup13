import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import Button from "react-bootstrap/Button";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {
  resetUpdates,
  saveRubric,
  saveRubricTemp,
  setEditingRubric,
  setSelectedElement
} from "../../redux/rubric/actions";


class RubricBottomBar extends Component {
  constructor (props) {
    super(props);
  }


  render () {
    return (
      <div className={styles.controlBottomBar}>
        <div>
          <Button variant="linkLightGray" onClick={this.onClickCancelButton}>Cancel</Button>
          <Button variant="lightGreen" onClick={this.onClickSaveButton}>Save</Button>
        </div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubricTemp: state.rubric.rubricTemp,
    rubric: state.rubric.rubric,
    updates: state.rubric.updates
  };
};

const actionCreators = {
  saveRubric,
  saveRubricTemp,
  setEditingRubric,
  setSelectedElement,
  resetUpdates
}

export default connect(mapStateToProps, actionCreators)(RubricBottomBar)


















