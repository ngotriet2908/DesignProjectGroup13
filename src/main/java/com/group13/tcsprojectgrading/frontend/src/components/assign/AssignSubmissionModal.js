import {Button, Spinner, Modal, Alert} from 'react-bootstrap'
import React, {Component} from "react";
import styles from "./assign.module.css";
import {request} from "../../services/request";
import {BASE, PROJECT, USER_COURSES} from "../../services/endpoints";
import {connect} from "react-redux";
import {IoCloseOutline, IoCheckboxOutline, IoSquareOutline} from "react-icons/io5";
import classnames from 'classnames';


class AssignSubmissionModal extends Component {
  constructor(props) {
    super(props);
  }

  onSave = () => {
    // determine choice (unassign vs assign)
    if (this.props.choice == null) {
      this.props.onReturnTask(this.props.taskGroup)
    } else {
      this.props.onAccept(this.props.taskGroup, this.props.choice)
    }
  }

  createFirstRow = () => {
    let result = [];

    result.push(
      <div className={
        classnames(styles.modalBodyContainerRow,
          this.props.choice == null && styles.modalBodyContainerRowActive)
      } onClick={() => this.props.setModalAssignChoice(null)}
      key="unassigned">
        {this.props.choice == null ?
          <IoCheckboxOutline size={16}/>
          :
          <IoSquareOutline size={16}/>
        }
        <span>Unassigned</span>
      </div>
    )

    if (this.props.currentGrader != null) {
      const eq = this.props.choice != null && this.props.choice.id === this.props.currentGrader.id;

      result.push(
        <div className={classnames(styles.modalBodyContainerRow,
          eq && styles.modalBodyContainerRowActive)}
        onClick={() => this.props.setModalAssignChoice(this.props.currentGrader)}
        key="current">
          {eq ?
            <IoCheckboxOutline size={16}/>
            :
            <IoSquareOutline size={16}/>
          }
          <span>{this.props.currentGrader.name} - {this.props.currentGrader.groups.length} task(s)</span>
        </div>
      )
    }

    return result;
  }

  render() {
    return(
      <Modal
        centered
        backdrop="static"
        size="lg"
        show={this.props.show}
        onHide={this.props.onClose}
        animation={false}
      >
        <div className={styles.modalContainer}>
          <div className={styles.modalHeaderContainer}>
            <h2>Assign</h2>
            <div className={styles.modalHeaderContainerButton} onClick={() => this.props.onClose()}>
              <IoCloseOutline size={30}/>
            </div>
          </div>

          <div className={styles.modalDescriptionContainer}>
            <div>
              Choose a person who will be responsible for grading the submission of <b>'{(this.props.taskGroup != null)? this.props.taskGroup.name : null}'</b> or leave the submission unassigned.
            </div>
          </div>

          {/* body */}
          <div className={styles.modalBodyContainer}>

            {this.createFirstRow()}

            {this.props.graders.filter((grader) => {
              return (this.props.currentGrader == null) || (grader.id !== this.props.currentGrader.id)
            }).map((grader) => {
              const eq = this.props.choice != null && this.props.choice.id === grader.id;

              return (
                <div className={classnames(styles.modalBodyContainerRow, eq && styles.modalBodyContainerRowActive)}
                  key={grader.id}
                  onClick={() => this.props.setModalAssignChoice(grader)}>
                  {eq ?
                    <IoCheckboxOutline size={16}/>
                    :
                    <IoSquareOutline size={16}/>
                  }
                  <span>{grader.name} - {grader.groups.length} task(s)</span>
                </div>
              )
            })
            }
          </div>

          {/* footer */}
          <div className={styles.modalFooterContainer}>
            <div className={styles.modalFooterContainerButtonGroup}>
              <Button variant="linkLightGray" onClick={() => this.props.onClose()}>
                Cancel
              </Button>

              <Button variant="lightGreen"
                onClick={() => this.onSave()}>
                Save
              </Button>
            </div>
          </div>
        </div>
      </Modal>
    )
  }
}

export default connect(null, null)(AssignSubmissionModal)