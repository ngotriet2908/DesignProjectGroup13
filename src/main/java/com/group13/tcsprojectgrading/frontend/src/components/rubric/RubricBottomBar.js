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
    console.log("Save rubric.");
    console.log(this.props.updates);

    if (this.props.updates.length === 0) {
      this.props.saveRubricTemp(null);
      this.props.setEditingRubric(false);
      this.props.resetUpdates();
    } else {
      request(BASE + "courses/" + this.props.courseId + "/projects/" + this.props.projectId + "/rubric", "PATCH", this.props.updates)
        .then(data => {
          console.log(data);

          if (data.status === 200) {
            this.props.setEditingRubric(false);
            this.props.saveRubricTemp(null);
            this.props.resetUpdates();
          } else {
            console.log("Error updating rubric.")
          }
        })
        .catch(error => {
          console.error(error.message);
        });

      // request(BASE + "courses/" + this.props.courseId + "/projects/" + this.props.projectId + "/rubric", "POST", this.props.rubric)
      //   .then(data => {
      //     console.log(data);
      //
      //     if (data.status === 200) {
      //       this.props.setEditingRubric(false);
      //       this.props.saveRubricTemp(null);
      //       this.props.resetUpdates();
      //     } else {
      //       console.log("Error updating rubric.")
      //     }
      //   })
      //   .catch(error => {
      //     console.error(error.message);
      //   });
    }
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


















