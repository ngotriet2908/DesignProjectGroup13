import {Button, Card, FormControl, Modal, Form} from 'react-bootstrap'
import React, {Component} from "react";

class BulkAssignModal extends Component {
  constructor(props) {
    super(props);
    this.state = {

    }
  }

  render() {
    return(<Modal centered
                  backdrop="static"
                  size="lg"
                  show={this.props.show}
                  onHide={this.props.onClose}>
      <Modal.Header closeButton>
        <Modal.Title>Group Details</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {
          <div>
            <h6>Task: {(this.props.taskGroup != null)? this.props.taskGroup.name : null}</h6>
            <h6>Current grader: {(this.props.isFromNotAssigned)? "None" :
              (this.props.currentGrader != null)? this.props.currentGrader.name : null}</h6>
            <h6>Current choice for assigning task: {(this.state.choice != null)? this.state.choice.name : null}</h6>
            <h3> </h3>
            <Form.Group controlId="graderSelect">
              <Form.Label>Possible Candidate List</Form.Label>
              <Form.Control as="select"
                            multiple
                            onChange={this.onSelectChange}>
                {this.props.graders
                  .filter((grader) => {
                    return (this.props.isFromNotAssigned)
                      || (this.props.currentGrader != null) && (grader.name !== this.props.currentGrader.name)
                  })
                  .map((grader) => {
                    return <option key={grader.id} value={grader.id}>name: {grader.name}, current tasks: {grader.groups.length}</option>
                  })
                }
              </Form.Control>
            </Form.Group>
          </div>
        }
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary"
                onClick={() => {
                  this.setState({choice:null})
                  this.props.onClose()
                }}>
          Cancel
        </Button>

        {(this.props.isFromNotAssigned)? null :
          <Button variant="danger"
                  onClick={() => {
                    this.setState({choice:null})
                    this.props.onClose()
                    this.props.onReturnTask()
                  }}>
            Return Task
          </Button>}

        <Button disabled={this.state.choice == null} variant="primary"
                onClick={() =>
                {
                  this.setState({choice:null})
                  this.props.onAccept(this.state.choice)
                }}>
          {(this.props.isFromNotAssigned)?"Assign":"Re-Assign"}
        </Button>
      </Modal.Footer>
    </Modal>)
  }
}

export default BulkAssignModal