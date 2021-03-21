import {Button, Modal, Form, InputGroup, FormControl, Card, Alert, ListGroup} from 'react-bootstrap'
import React, {Component} from "react";
import FlagModalFlagView from "./FlagModalFlagView";

class FlagModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      createShow: false,
      name:"",
      description:"",
      error:"",
      color: "primary",
    }
  }

  showFlagCreationHandler = () => {
    this.setState((prevState) => {
      return {
        createShow: !prevState.createShow,
        name:"",
        description:"",
        error:"",
        color: "primary",
      }
    })
  }

  onNameChange = (event) => {
    this.setState({
      name: event.target.value
    })
  }

  onColorChange = (event) => {
    this.setState({
      color: event.target.value
    })
  }

  onDescriptionChange = (event) => {
    this.setState({
      description: event.target.value
    })
  }

  createFlagHandler = async () => {
    let response = await this.props.createFlagHandler(this.state.name, this.state.description, this.state.color)
    console.log("flagModal: " + response)
    if (response === "ok") {
      this.setState({
        createShow: false,
        name:"",
        description:"",
        error:"",
        color: "primary",
      })
    } else {
      console.log(response)
      this.setState({
        error: response
      })
    }
  }

  removeFlagHandler = async (id) => {
    let response = await this.props.removeFlagHandler(id)
    console.log("flagModal: " + response)
    if (response === "ok") {
      console.log(response)
    } else {
      console.log(response)
      this.setState({
        error: response
      })
    }
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
                          <FlagModalFlagView current={true} flag={flag} removeFlag={() => this.props.removeFlag(flag)}/>
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
                      this.props.user.flags
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
                                                 addFlag={() => this.props.addFlag(flag)}
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
                      <Form>
                        <Form.Group>
                          <Form.Label>Flag Name</Form.Label>
                          <Form.Control type="text" placeholder="name" onChange={this.onNameChange}/>
                        </Form.Group>
                        <Form.Group>
                          <Form.Label>Description</Form.Label>
                          <Form.Control as="textarea" rows={3} onChange={this.onDescriptionChange}/>
                        </Form.Group>
                        <Form.Group>
                          <Form.Label>Select color</Form.Label>
                          <Form.Control onChange={this.onColorChange} as="select">
                            <option value="primary">blue</option>
                            <option value="secondary">gray</option>
                            <option value="success">green</option>
                            <option value="danger">red</option>
                            <option value="warning">orange</option>
                            <option value="info">cyan</option>
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