import {Button, Modal, Form, InputGroup, FormControl, Card, Alert, ListGroup} from 'react-bootstrap'
import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import globalStyles from "../helpers/global.module.css";
import classnames from "classnames";
import {IoCheckboxOutline, IoCloseOutline, IoSquareOutline} from "react-icons/io5";
import Spinner from "react-bootstrap/Spinner";
import styles from "./submissionDetails.module.css";
import ButtonTooltip from "../helpers/ButtonTooltip";

class AddParticipantModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoaded: false,
    }

    this.formRef = React.createRef();
  }

  searchArray(students, student) {
    let i;
    for(i = 0; i < students.length; i++) {
      if (students[i].id === student.id) {
        return true
      }
    }
    return false
  }

  filterCurrentStudents(allStudents, currentStudents) {
    return allStudents.filter((student) => {
      return !this.searchArray(currentStudents, student)
    })
  }

  fetchStudents = () => {
    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/participants/students`)
      .then(async response => {
        let data = await response.json();

        let students = this.filterCurrentStudents(data, this.props.currentStudents);

        this.setState({
          students: students,
          isLoaded: true,
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  onClose = () => {
    this.setState({
      isLoaded: false,
      students: [],
    })

    this.props.toggleShow();
  }

  onAccept = () => {
    let studentId = this.formRef.current.studentSelector.value

    if (studentId === "null") {
      return;
    }

    let assessmentId = this.formRef.current.assessmentSelector.value

    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/addParticipant/${studentId}/${assessmentId}`,
      "POST")
      .then(async response => {
        let data = await response.json();

        this.setState({
          students: [],
          isLoaded: false,
        })

        this.props.toggleShow();
        // this.props.reloadPage();

        this.props.updateSubmission(data)
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render() {
    return(
      <Modal
        centered
        backdrop="static"
        size="lg"
        onShow={this.fetchStudents}
        show={this.props.show}
        onHide={this.onClose}
        animation={false}
      >
        <div className={globalStyles.modalContainer}>
          <div className={globalStyles.modalHeaderContainer}>
            <h2>Add student</h2>
            <ButtonTooltip className={classnames(globalStyles.modalHeaderContainerButton, globalStyles.modalHeaderContainerCloseButton)}
                           content="Close" placement="top" onClick={this.onClose}>
              <IoCloseOutline size={30}/>
            </ButtonTooltip>
          </div>

          <div className={globalStyles.modalDescriptionContainer}>
            <div>Manually associate a student with a submission.</div>
          </div>

          {!this.state.isLoaded ?
            <div className={globalStyles.modalSpinnerContainer}>
              <Spinner className={globalStyles.modalSpinner} animation="border" role="status">
                <span className="sr-only">Loading...</span>
              </Spinner>
            </div>
            :
            //body
            <div className={globalStyles.modalBodyContainer}>
              <Form ref={this.formRef} className={styles.gradeEditorContentContainer}>
                <Form.Group controlId="studentSelector" className={styles.gradeEditorCardItem}>
                  <Form.Label>Select student</Form.Label>
                  <Form.Control as="select">
                    <option value={"null"}>None</option>
                    {this.state.students.map((student) => {
                      return (
                        <option
                          value={student.id}
                          key={student.id}
                        >
                          {student.name}
                        </option>
                      )
                    })}
                  </Form.Control>
                </Form.Group>

                <Form.Group controlId="assessmentSelector" className={styles.gradeEditorCardItem}>
                  <Form.Label>Select assessment</Form.Label>
                  <Form.Control as="select">
                    {this.props.currentAssessments.map((assessment) => {
                      return (
                        <option
                          value={assessment.id}
                          key={assessment.id}
                        >
                          {assessment.id}
                        </option>
                      )
                    })}
                  </Form.Control>
                </Form.Group>

              </Form>
            </div>
          }

          <div className={classnames(globalStyles.modalFooterContainer)}>
            <div className={globalStyles.modalFooterContainerButtonGroup}>
              <Button variant="linkLightGray" onClick={this.onClose}>Cancel</Button>
              <Button variant="lightGreen" onClick={this.onAccept}>Save</Button>
            </div>
          </div>
        </div>
      </Modal>
    )
  }
}

export default AddParticipantModal