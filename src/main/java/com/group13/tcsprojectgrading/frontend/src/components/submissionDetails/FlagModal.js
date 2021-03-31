import {Button, Modal, Form, InputGroup, FormControl, Card, Alert, ListGroup} from 'react-bootstrap'
import React, {Component} from "react";
import FlagModalFlagView from "./FlagModalFlagView";
import {request} from "../../services/request";

class FlagModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      createShow: false,
      error: ""
    }
    this.formRef = React.createRef()
  }

  addFlagHandler = (flag) => {
    request(`/api/courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/submissions/${this.props.params.submissionId}/flag`
      , "POST", flag)
      .then((response) => {
        return response.json()
      })
      .then((data) => {
        console.log(data)
        if (data.error !== undefined) {
          console.log(data.error)
          this.setState({
            error: data.error
          })
        } else {
          this.props.updateSubmissionFlags(data.data)
          this.setState({
            createShow: false,
            error: ""
          })
        }
      })
  }

  createFlagHandler = () => {
    let object = {
      "name": this.formRef.current.nameInput.value,
      "description": this.formRef.current.descriptionInput.value,
      "variant": this.formRef.current.colorSelector.value,
    }
    request(`/api/courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/submissions/${this.props.params.submissionId}/flag/create`
      , "POST", object)
      .then((response) => {
        return response.json()
      })
      .then((data) => {
        console.log(data)
        if (data.error !== undefined) {
          console.log(data.error)
          this.setState({
            error: data.error
          })
        } else {
          this.props.updateProjectFlags(data.data)
          this.setState({
            createShow: false,
            error: ""
          })
        }
      })
  }

  removeFlagHandler = (id) => {
    request(`/api/courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/flag/${id}`
      , "DELETE")
      .then((response) => {
        return response.json()
      })
      .then((data) => {
        console.log(data)
        if (data.error !== undefined) {
          console.log(data.error)
          this.setState({
            error: data.error
          })
        } else {
          this.props.updateProjectFlags(data.data)
          this.setState({
            createShow: false,
            error: ""
          })
        }
      })
  }

  uncheckFlagHandler = (flag) => {
    request(`/api/courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/submissions/${this.props.params.submissionId}/flag/${flag.id}`
      , "DELETE")
      .then((response) => {
        return response.json()
      })
      .then((data) => {
        console.log(data)
        if (data.error !== undefined) {
          console.log(data.error)
          this.setState({
            error: data.error
          })
        } else {
          this.props.updateSubmissionFlags(data.data)
          this.setState({
            createShow: false,
            error: ""
          })
        }
      })
  }

  showFlagCreationHandler = () => {
    this.setState((prevState) => {
      return {
        createShow: !prevState.createShow,
      }
    })
  }

  handleCloseError = () => {
    this.setState({
      error: ""
    })
  }

  render() {
    return(
      <Modal centered
        backdrop="static"
        size="lg"
        show={this.props.show}
        onHide={this.props.onClose}
        animation={false}
        // onEnter={this.onShowHandle}
        // onExited={this.onExitHandle}
      >
        <Modal.Header closeButton>
          <Modal.Title>Flag manager</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div>
            <Card>
              <Card.Body>
                <h5>Current flag</h5>
                <ListGroup>
                  {
                    this.props.flags.map((flag) => {
                      return (
                        <ListGroup.Item key={flag.id}>
                          <FlagModalFlagView
                            current={true}
                            flag={flag}
                            removeFlag={() => this.uncheckFlagHandler(flag)}/>
                        </ListGroup.Item>
                      )
                    })
                  }
                </ListGroup>
              </Card.Body>
            </Card>

            <Card>
              <Card.Body>
                <div style={{marginBottom: "0.6rem"}}>
                  <h5 style={{display: "inline"}}>Available flag</h5>
                  <Button style={{marginLeft: "0.6rem"}}
                    variant={(this.state.createShow)? "danger":"primary"}
                    size="sm"
                    onClick={this.showFlagCreationHandler}>
                    {(this.state.createShow)? "Cancel" : "Create new flag"}
                  </Button>
                </div>
                {(!this.state.createShow) ?
                  (
                    <ListGroup>
                      {
                        this.props.availableFlags
                          .filter((flag) => {
                            let i;
                            for (i = 0; i < this.props.flags.length; i++) {
                              if (flag.id === this.props.flags[i].id) return false
                            }
                            return true
                          })
                          .map((flag) => {
                            return (
                              <ListGroup.Item key={flag.id}>
                                <FlagModalFlagView current={false}
                                  flag={flag}
                                  addFlag={() => this.addFlagHandler(flag)}
                                  removeFlagPermanently={() => this.removeFlagHandler(flag.id)}
                                />
                              </ListGroup.Item>
                            )
                          })
                      }
                    </ListGroup>)
                  :
                  (
                    <div>
                      {
                        (this.state.error.length <= 0)? null :
                          <Alert variant="danger" onClose={this.handleCloseError} dismissible>
                            <p> Flag name: {this.state.name} is already existed </p>
                          </Alert>
                      }
                      <Form ref={this.formRef}>
                        <Form.Group controlId="nameInput">
                          <Form.Label>Flag Name</Form.Label>
                          <Form.Control type="text" placeholder="name" />
                        </Form.Group>
                        <Form.Group controlId="descriptionInput">
                          <Form.Label>Description</Form.Label>
                          <Form.Control as="textarea" rows={3}/>
                        </Form.Group>
                        <Form.Group controlId="colorSelector">
                          <Form.Label>Select color</Form.Label>
                          <Form.Control as="select">
                            {/*<option value="secondary">gray</option>*/}
                            <option value="green">green</option>
                            <option value="tomato">red</option>
                            <option value="orange">orange</option>
                            {/*<option value="info">cyan</option>*/}
                            <option value="light">white</option>
                            <option value="dark">black</option>
                          </Form.Control>
                        </Form.Group>
                        <Button variant="primary" onClick={this.createFlagHandler}>
                          Create
                        </Button>
                      </Form>
                    </div>

                  )
                }
              </Card.Body>
            </Card>


          </div>
        </Modal.Body>
      </Modal>
    )
  }
}

export default FlagModal;