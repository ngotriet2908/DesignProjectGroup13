import {Button, Modal, Form, InputGroup, FormControl, Card, Alert} from 'react-bootstrap'
import React, {Component} from "react";
import styles from "./assign.module.css"

class BulkAssignModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      graders: [],
      notAssigned: [],
      numTasks: 0,
      showAlert: false,
      alertBody: "",
    }
  }

  onExitHandle = () => {
    console.log("exit")
  }

  onShowHandle = () => {
    console.log("show")
    let graders = [...this.props.graders]
    let notAssigned = [...this.props.notAssigned]
    graders.forEach((grader) => {
      grader["checked"] = false
    })
    this.setState({
      graders: graders,
      notAssigned: notAssigned,
      numTasks: 0,
      showAlert: false,
      alertBody: "",
    })
  }

  handleCheckedChangeHandler = (grader, event) => {
    // console.log(event.target.checked)
    let graders = [...this.state.graders]
    graders.forEach((grader1) => {
      if (grader1.id === grader.id) {
        grader1.checked = event.target.checked
      }
    })
    // console.log(graders)
    this.setState({
      graders: graders
    })
  }

  getChosenNum() {
    let graders = [...this.state.graders]
    let count = 0;
    graders.forEach((grader) => {
      if (grader.checked != null && grader.checked) {
        count += 1
      }
    })
    return count
  }

  handleSelectAll = (event) => {
    let graders = [...this.state.graders]
    graders.forEach((grader) => {
      grader.checked = event.target.checked
    })
    this.setState({
      graders: graders
    })
  }

  handleChangedAssignedWantToBeTask = (event) => {
    this.setState({
      numTasks: event.target.value
    })
  }


  handleOnClick = () => {
    if ((this.state.numTasks > 0)
      && (this.state.notAssigned.length > 0)
      && (this.getChosenNum() > 0)
      && (this.state.notAssigned.length >= this.state.numTasks)
    ) {
      let object = {
        tasks: this.state.numTasks,
        graders: this.state.graders.filter((grader) => {
          return grader.checked
        })
      }
      this.props.onAccept(object)
    } else {
      let message = "" + ((this.state.numTasks > 0)? "" : "number of tasks need to be larger than 0, \n") +
                    ((this.state.notAssigned.length > 0)? "" : "number of not assigned tasks need to be larger than 0, \n") +
                    ((this.getChosenNum() > 0)? "" : "number of chosen graders needs to be larger than 0, \n") +
                    ((this.state.notAssigned.length >= this.state.numTasks)? "" : "number of tasks need to be assigned have to be smaller or equal than number of available tasks");
      console.log(message)
      this.setState({
        showAlert: true,
        alertBody: message,
      })
    }
  }

  closeAlertHandle = () => {
    this.setState({
      showAlert: false,
      alertBody: "",
    })
  }

  render() {
    return (
      <Modal centered
             backdrop="static"
             size="lg"
             show={this.props.show}
             onHide={this.props.onClose}
             onEnter={this.onShowHandle}
             onExited={this.onExitHandle}>
        <Modal.Header closeButton>
          <Modal.Title>Bulk Assign</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {
            <div>
              {(!this.state.showAlert)? null:
                <Alert variant="danger" onClose={this.closeAlertHandle} dismissible>
                  <p>
                    {this.state.alertBody}
                  </p>
                </Alert>}
              <h6>Number of available graders: {this.state.graders.length}</h6>
              <h6>Number of not assigned tasks: {this.state.notAssigned.length}</h6>

              <h6>Number of chosen graders: {this.getChosenNum()}</h6>
              <InputGroup>
                <InputGroup.Prepend>
                  <InputGroup.Text>Number of want to assign tasks</InputGroup.Text>
                </InputGroup.Prepend>
                <FormControl onChange={this.handleChangedAssignedWantToBeTask} placeholder="Tasks"/>
              </InputGroup>

              <Card>
                <Card.Body>
                  <Card.Title>
                    Available graders
                  </Card.Title>
                  <Form.Check
                    type={"checkbox"}
                    label={"select all"}
                    onChange={this.handleSelectAll}
                    id={"select-all"}
                  />
                  {this.state.graders.map((grader) => {
                    return (
                      <Form.Check
                        type={"checkbox"}
                        label={"name: " + grader.name + ", current task: " + grader.groups.length}
                        checked={grader.checked}
                        onChange={(event) => this.handleCheckedChangeHandler(grader, event)}
                        id={"checkbox-" + grader.id}
                      />
                    )
                  })}
                </Card.Body>
              </Card>
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

          <Button variant="primary" onClick={this.handleOnClick}>
            Assign
          </Button>
        </Modal.Footer>
      </Modal>
    )
  }
}

export default BulkAssignModal