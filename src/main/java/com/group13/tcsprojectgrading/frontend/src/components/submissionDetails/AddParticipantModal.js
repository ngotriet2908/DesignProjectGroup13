import {Button, Modal, Form, InputGroup, FormControl, Card, Alert, ListGroup} from 'react-bootstrap'
import React, {Component} from "react";
import FlagModalFlagView from "./FlagModalFlagView";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";

class AddParticipantModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      error: ""
    }
    this.formRef = React.createRef()
  }

  addParticipantHandler = () => {
    let pId = this.formRef.current.participantSelector.value
    if (pId === "null") {
      this.props.onClose()
      return
    }
    let aId = this.formRef.current.assessmentSelector.value
    request(`${BASE}courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/submissions/${this.props.params.submissionId}/addParticipant/${pId}/${aId}`, "POST")
      .then(response => {
        if (response.ok) {
          return response.json()
        } else {
          throw new Error(response.statusText)
        }
      })
      .then(data => {
          this.props.updateSubmission(data)
      })
      .catch(error => {
        this.setState({
          error: error.message
        })
        console.error(error.message);
      });

  }

  render() {
    return (
      <Modal centered
             backdrop="static"
             size="lg"
             show={this.props.show}
             onHide={this.props.onClose}
             // animation={false}
        // onEnter={this.onShowHandle}
        // onExited={this.onExitHandle}
      >
        <Modal.Header closeButton>
          <Modal.Title>Add participant</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div>
            <Form ref={this.formRef}>
              <Form.Group controlId="participantSelector">
                <Form.Label>Select participant</Form.Label>
                <Form.Control as="select">
                  <option value={"null"}>None</option>
                  {this.props.participants.map((participant) => {
                    return (
                      <option value={participant.id}>{participant.name}</option>
                    )
                  })}
                </Form.Control>
              </Form.Group>
              <Form.Group controlId="assessmentSelector">
                <Form.Label>Select assessment</Form.Label>
                <Form.Control as="select">
                  {this.props.assessments.map((assessment) => {
                    return (
                      <option value={assessment.id}>{assessment.id}</option>
                    )
                  })}
                </Form.Control>
              </Form.Group>
              <Button variant="primary" onClick={this.addParticipantHandler}>
                Add
              </Button>
            </Form>
          </div>
        </Modal.Body>
      </Modal>
    );
  }
}

export default AddParticipantModal