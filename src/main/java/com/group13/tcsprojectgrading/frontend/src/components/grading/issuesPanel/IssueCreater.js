import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import {request} from "../../../services/request";
import {Breadcrumb, Button, Spinner, InputGroup, Form, Card, Modal} from "react-bootstrap";

class IssueCreater extends Component {
  constructor(props) {
    super(props);
    this.formRef = React.createRef();
  }

  componentDidMount() {
    console.log(this.props.rubric)
  }

  submitHandler = (event) => {
    console.log(this.formRef.current.targetSelector.value)
    let obj = {
      target: this.formRef.current.targetSelector.value.split("/")[0],
      targetName: this.formRef.current.targetSelector.value.split("/")[2],
      targetType: this.formRef.current.targetSelector.value.split("/")[1],
      reference: this.formRef.current.refSelector.value,
      subject: this.formRef.current.subjectInput.value,
      description: this.formRef.current.descriptionInput.value,
      addressee: this.formRef.current.addresseeSelector.value,
    }
    console.log(obj)
    this.props.createIssue(obj)
  }

  visitTree(node, result) {
    let obj = {
      id: node.content.id,
      type: node.content.type,
      name: (node.content.type === "0")? "Block " + node.content.title : "Criterion " + node.content.title
    }
    result.push(obj)
    if (node.hasOwnProperty("children")) {
      node.children.forEach((node) => {
        this.visitTree(node, result)
      })
    }
  }

  getAllElements(rubric) {
    let result = []
    rubric.children.forEach((node) => {
      this.visitTree(node, result)
    })
    console.log(result)
    return result
  }

  render() {
    return(
      <Card>
        <Card.Body>
          <Card.Title>
            Create Issue
          </Card.Title>
          <Form ref={this.formRef}>
            <Form.Group controlId="targetSelector">
              <Form.Label>Select target</Form.Label>
              <Form.Control as="select">
                <option value={this.props.params.assessmentId +"/2/this assessment"}>this assessment</option>
                {this.getAllElements(this.props.rubric).map((element) => {
                  return (
                    <option value={element.id +"/"+element.type + "/" + element.name}>{element.name}</option>
                  )
                })}
              </Form.Control>
            </Form.Group>

            <Form.Group controlId="refSelector">
              <Form.Label>Select reference issue</Form.Label>
              <Form.Control as="select">
                <option value={"null"}>no reference</option>
                {this.props.issues.map((issue) => {
                  return (
                    <option value={issue.id}>{issue.subject} on {issue.targetName}</option>
                  )
                })}
              </Form.Control>
            </Form.Group>

            <Form.Group controlId="addresseeSelector">
              <Form.Label>Select Addressee</Form.Label>
              <Form.Control as="select">
                <option value={"null"}>no addressee</option>
                {this.props.graders.map((grader) => {
                  return (
                    <option value={grader.id}>{grader.name}</option>
                  )
                })}
              </Form.Control>
            </Form.Group>

            <Form.Group controlId="subjectInput">
              <Form.Label>Subject</Form.Label>
              <Form.Control type="text" placeholder="Enter subject" />
            </Form.Group>

            <Form.Group controlId="descriptionInput">
              <Form.Label>Description</Form.Label>
              <Form.Control as="textarea" rows={3} placeholder="Enter description of the issue"/>
            </Form.Group>

            <Button variant="primary" onClick={this.props.cancelCreateIssue}>
              Cancel
            </Button>
            <Button variant="primary" onClick={this.submitHandler}>
              Create Issue
            </Button>
          </Form>
        </Card.Body>
      </Card>
    )
  }

}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(IssueCreater)