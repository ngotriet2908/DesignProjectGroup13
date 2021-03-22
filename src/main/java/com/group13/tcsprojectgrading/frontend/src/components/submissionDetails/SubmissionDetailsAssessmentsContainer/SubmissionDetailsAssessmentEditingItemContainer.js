import React, {Component} from "react";
import styles from "../submissionDetails.module.css";
import {request} from "../../../services/request";
import {BASE} from "../../../services/endpoints";
import {Card, Breadcrumb, Button, ListGroup, ListGroupItem, Form, Modal, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import store from "../../../redux/store";
import {URL_PREFIX} from "../../../services/config";
import {push} from "connected-react-router";
import {Link} from "react-router-dom";

class SubmissionDetailsAssessmentEditingItemContainer extends Component {
  constructor(props) {
    super(props);
    this.state = {
      show: false,
      currentParticipant: {}
    }
    this.selectorRef = React.createRef()
  }

  componentDidMount() {
    this.setState({
      show: false,
      currentParticipant: {}
    })
  }

  handleShowMoveModal = (participant) => {
    this.setState({
      show: true,
      currentParticipant: participant,
    })
  }

  handleCloseMoveModal = () => {
    this.setState({
      show: false,
      currentParticipant: {}
    })
  }

  handleMove = () => {
    let des = this.selectorRef.current.destinationAssessmentSelector.value
    let src = this.props.assessment.id
    let participant = this.state.currentParticipant.id
    this.props.handleMove(src, des, participant)
    this.setState({
      show: false,
      currentParticipant: {}
    })
  }

  render() {
    return (
      <div className={styles.memberAssessmentItem}>
        <div className={styles.assessmentCardHalf}>
          <div>
            <h6>id: {this.props.assessment.id}</h6>
            <h6>issues count: {this.props.assessment.issuesCount}</h6>
          </div>
          <div>
            <div style={{display:"inline", float:"right"}}>
              <Button onClick={this.props.handleClone} style={{display:"inline", marginLeft:"1rem"}}
                      variant="success"> Clone </Button>
              <Button onClick={this.props.handleDelete} style={{display:"inline", marginLeft:"0.5rem"}}
                      variant="danger"> Delete </Button>
            </div>
          </div>
        </div>
        <ListGroup>
          {this.props.assessment.participants.map((participant) => {
            return (
              <ListGroupItem key={"a-"+participant.id}>
                <div className={styles.memberEditingItem}>
                  <h6>name: {participant.name}</h6>
                  <h6>name: {participant.sid}</h6>
                  <Button onClick={() => this.handleShowMoveModal(participant)}
                          style={{float:"right", marginLeft:"0.5rem"}}
                          variant="warning"> Move </Button>
                </div>
              </ListGroupItem>)
          })}
        </ListGroup>
        <Modal centered show={this.state.show} onHide={this.handleCloseMoveModal}>
          <Modal.Header closeButton>
            <Modal.Title>Modal heading</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <h6>Participant: {this.state.currentParticipant.name}</h6>
            <Form ref={this.selectorRef}>
              <Form.Group controlId="destinationAssessmentSelector">
                <Form.Label>Example select</Form.Label>
                <Form.Control as="select">
                  {this.props.assessments.map((assessment) => {
                    if (assessment.id === this.props.assessment.id) {
                      return <option key={assessment.id} value={assessment.id}>{assessment.id} (current)</option>
                    } else {
                      return <option key={assessment.id} value={assessment.id}>{assessment.id}</option>
                    }
                  })}
                </Form.Control>
              </Form.Group>
            </Form>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={this.handleCloseMoveModal}>
              Close
            </Button>
            <Button variant="primary" onClick={this.handleMove}>
              Save Changes
            </Button>
          </Modal.Footer>
        </Modal>
      </div>
    );
  }

}

export default SubmissionDetailsAssessmentEditingItemContainer
