import React, { Component } from 'react'

import styles from './rubric.module.css'
import {connect} from "react-redux";
import Button from "react-bootstrap/Button";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {saveRubric, saveRubricTemp, setEditingRubric, setSelectedElement} from "../../redux/rubricNew/actions";


class RubricBottomBar extends Component {
  constructor (props) {
    super(props);
  }

  onClickCancelButton = () => {
    console.log("Cancel save.")

    // get rubric backup
    let rubricBackup = this.props.rubricTemp;

    this.props.setSelectedElement(rubricBackup.id);
    this.props.saveRubricTemp(null);
    this.props.saveRubric(rubricBackup);
    this.props.setEditingRubric(false);
  }

  onClickSaveButton = () => {
    console.log("Save rubric.")

    // send request
    request(BASE + "courses/" + this.props.courseId + "/projects/" + this.props.projectId + "/rubric", "POST", this.props.rubric)
      .then(data => {
        console.log(data);
        this.props.saveRubricTemp(null);
        this.props.setEditingRubric(false);
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render () {
    return (
      <div className={styles.controlBottomBar}>
        <div>
          <Button variant="link" onClick={this.onClickCancelButton}>Cancel</Button>
          <Button variant="success" onClick={this.onClickSaveButton}>Save</Button>
        </div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubricTemp: state.rubricNew.rubricTemp,
    rubric: state.rubricNew.rubric,
  };
};

const actionCreators = {
  saveRubric,
  saveRubricTemp,
  setEditingRubric,
  setSelectedElement,
}

export default connect(mapStateToProps, actionCreators)(RubricBottomBar)


















