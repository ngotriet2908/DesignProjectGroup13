import React, {Component, useState} from "react";
import styles from "../submissionDetails.module.css";
import {request} from "../../../services/request";
import {BASE} from "../../../services/endpoints";
import {Card, Breadcrumb, Button, ListGroup, ListGroupItem, Form, Modal, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import classnames from "classnames";
import globalStyles from "../../helpers/global.module.css";
import {IoCopyOutline, IoTrashOutline, IoSwapHorizontal, IoAdd, IoHourglassOutline} from "react-icons/io5";
import ButtonTooltip from "../../helpers/ButtonTooltip";

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
        <div className={styles.memberAssessmentHeader}>
          <h4>
            Assessment #{this.props.assessment.id}
          </h4>

          <div className={styles.buttonGroup}>
            <ButtonTooltip className={classnames(globalStyles.iconButton, styles.dangerButton)}
                           content="Delete Assessment" placement="bottom" onClick={this.props.handleDelete}>
              <IoTrashOutline size={26}/>
            </ButtonTooltip>
          </div>

        </div>
        <div>
          <p>progress: {this.props.assessment.progress}%</p>
          <p>issues count: {this.props.assessment.issues.length}</p>
        </div>
        <div>
          <p>Members:</p>
          <ListGroup>
            {this.props.assessment.members.map((participant) => {
              return (
                <ListGroupItem key={"a-"+participant.id}>
                  <div className={styles.memberEditingItem}>
                    <div className={styles.memberEditingItemHeader}>
                      <h5>{participant.name}</h5>
                      <div className={styles.buttonGroup}>
                        {
                          (participant.isCurrentAssessment)? null :
                            <ButtonTooltip className={classnames(globalStyles.iconButton, styles.primaryButton)}
                                           content="Set as Current" placement="bottom"
                                           onClick={() => this.props.handleActivateCurrent(this.props.assessment, participant.id)}>
                              <IoHourglassOutline size={26}/>
                            </ButtonTooltip>
                        }

                        {
                          (this.props.assessment.members.length <= 1)? null :
                            <ButtonTooltip className={classnames(globalStyles.iconButton, styles.primaryButton)}
                                           content="Create new Assessment for this Student" placement="bottom"
                                           onClick={() => this.props.handleCreateNew(participant.id)}>
                              <IoAdd size={26}/>
                            </ButtonTooltip>
                        }

                        {
                          (this.props.assessment.members.length <= 1)? null :
                            <ButtonTooltip className={classnames(globalStyles.iconButton, styles.primaryButton)}
                                           content="Copy Assessment for this Student" placement="bottom"
                                           onClick={() => this.props.handleClone(this.props.assessment, participant.id)}>
                              <IoCopyOutline size={26}/>
                            </ButtonTooltip>
                        }

                        <ButtonTooltip className={classnames(globalStyles.iconButton, styles.neuterButton)}
                                       content="Move Student to another Assessment" placement="bottom"
                                       onClick={() => this.handleShowMoveModal(participant)}>
                          <IoSwapHorizontal size={26}/>
                        </ButtonTooltip>
                      </div>
                    </div>
                    <p>sid: {participant.sNumber}</p>
                    <p>Is current: {participant.isCurrentAssessment.toString()}</p>
                  </div>
                </ListGroupItem>)
            })}
          </ListGroup>
        </div>

        <Modal
          centered
          show={this.state.show}
          onHide={this.handleCloseMoveModal}
          animation={false}
        >
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