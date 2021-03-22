import React, { Component } from 'react'
import styles from '../grading.module.css'
import globalStyles from '../../helpers/global.module.css'
import {connect} from "react-redux";
import {Button, Form, Card} from "react-bootstrap";
import classnames from 'classnames';

class IssueCreator extends Component {
  constructor(props) {
    super(props);
    this.formRef = React.createRef();
  }

  submitHandler = () => {
    let obj = {
      target: this.formRef.current.targetSelector.value.split("/")[0],
      targetName: this.formRef.current.targetSelector.value.split("/")[2],
      targetType: this.formRef.current.targetSelector.value.split("/")[1],
      reference: this.formRef.current.refSelector.value,
      subject: this.formRef.current.subjectInput.value === "" ? "No title" : this.formRef.current.subjectInput.value,
      description: this.formRef.current.descriptionInput.value,
      addressee: this.formRef.current.addresseeSelector.value,
    }
    this.props.createIssue(obj)
  }

  visitTree(node, result) {
    let obj = {
      id: node.content.id,
      type: node.content.type,
      name: (node.content.type === "0")? "B: " + node.content.title : "C: " + node.content.title
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
    return result
  }

  render() {
    return(
      <>
        <div className={styles.gradingCardTitle}>
          <h4>Create issue</h4>
        </div>

        <Form ref={this.formRef} className={styles.gradeEditorContentContainer}>
          <Form.Group controlId="targetSelector" className={styles.gradeEditorCardItem}>
            <Form.Label>Select target</Form.Label>
            <Form.Control as="select">
              <option value={this.props.routeParams.assessmentId +"/2/this assessment"}>this assessment</option>
              {this.getAllElements(this.props.rubric).map((element) => {
                return (
                  <option value={element.id +"/"+element.type + "/" + element.name}>{element.name}</option>
                )
              })}
            </Form.Control>
          </Form.Group>

          <Form.Group controlId="refSelector" className={styles.gradeEditorCardItem}>
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

          <Form.Group controlId="addresseeSelector" className={styles.gradeEditorCardItem}>
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

          <Form.Group controlId="subjectInput" className={styles.gradeEditorCardItem}>
            <Form.Label>Subject</Form.Label>
            <Form.Control type="text" placeholder="Enter subject" />
          </Form.Group>

          <Form.Group controlId="descriptionInput" className={classnames(styles.gradeEditorCardItem, styles.gradeEditorCardItemFill)}>
            <Form.Label>Description</Form.Label>
            <Form.Control as="textarea" rows={3} placeholder="Enter description of the issue"/>
          </Form.Group>

          <div className={styles.gradeEditorCardFooter}>
            <Button className={styles.gradeEditorCardButton} variant="linkLightGray"
              onClick={() => this.props.toggleCreatingState()}>Cancel</Button>
            <Button className={styles.gradeEditorCardButton} variant="lightGreen"
              onClick={this.submitHandler}>Create</Button>
          </div>
        </Form>
      </>
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

export default connect(mapStateToProps, actionCreators)(IssueCreator)