import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import {request} from "../../../services/request";
import {Breadcrumb, Button, Spinner, InputGroup, Form, Card, Modal, Badge} from "react-bootstrap";


class IssueCard extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isExpanded: false,
      isSolving: false,
    }
    this.formRef = React.createRef()
  }

  expandHandler = () => {
    this.setState((prevState) => {
      return {
        isExpanded: !prevState.isExpanded
      }
    })
  }

  submitSolution = () => {
    console.log(this.formRef.current.solutionInput.value)
    let obj = {
      id: this.props.issue.id,
      solution: this.formRef.current.solutionInput.value
    }
    request(`/api/courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/submissions/${this.props.params.submissionId}/${this.props.params.assessmentId}/issues/resolve`, "POST", obj)
      .then((response) => {
        return response.json()
      })
      .then((data) => {
        this.props.updateIssues(data)
        this.setState({
          isSolving: false
        })
      })
  }

  cancelResolve = () => {
    this.setState({
      isSolving: false,
    })
  }

  startResolve = () => {
    this.setState({
      isSolving: true,
    })
  }

  render() {
    return (
      <Card>
        <Card.Body>
          <Card.Title>

            <h5>
              {this.props.issue.subject}
              {(this.props.issue.status === "resolved")?
                <Badge className={styles.badge} variant="success">resolved</Badge> :
                <Badge className={styles.badge} variant="danger">unresolved</Badge>
              }
            </h5>
          </Card.Title>
          {(this.state.isExpanded)?
            (
              <div>
                <h6>target: {this.props.issue.targetName}</h6>
                <h6>created by: {this.props.issue.creator.name}</h6>
                {(this.props.issue.hasOwnProperty("reference"))? <h6>reference issue: {this.props.issue.reference.subject}</h6> :null}
                <h6>subject: {this.props.issue.subject}</h6>
                <h6>description: {this.props.issue.description}</h6>
                {(this.props.issue.hasOwnProperty("addressee"))? <h6>addressee: {this.props.issue.addressee.name}</h6> :null}
                {(this.props.issue.hasOwnProperty("solution"))? <h6>solution: {this.props.issue.solution}</h6> :null}
              </div>
            ):
            (
              <div>
                <h6>target: {this.props.issue.targetName}</h6>
                <h6>created by: {this.props.issue.creator.name}</h6>
              </div>
            )
          }


          {(!this.state.isSolving)?
            <div>
              <Button onClick={this.expandHandler}>
                {(this.state.isExpanded)? "collapse":"expand"}
              </Button>
              {(this.props.issue.status === "unresolved")?
                <Button onClick={this.startResolve}>Resolve</Button> : null
              }
            </div> :
            <div>
              <Button onClick={this.expandHandler}>
                {(this.state.isExpanded)? "collapse":"expand"}
              </Button>

              <Card>
                <Card.Body>
                  <Form ref={this.formRef}>
                    <Form.Group controlId="solutionInput">
                      <Form.Label>Description</Form.Label>
                      <Form.Control as="textarea" rows={3} placeholder="Enter your solution"/>
                    </Form.Group>
                    <Button onClick={this.submitSolution}>
                      Submit Solution
                    </Button>
                    <Button variant="danger" onClick={this.cancelResolve}>
                      Cancel
                    </Button>
                  </Form>
                </Card.Body>
              </Card>
            </div>
          }

        </Card.Body>
      </Card>
    );
  }
}

export default IssueCard