import {Button, Spinner, Modal, Form} from 'react-bootstrap'
import React, {Component} from "react";
import globalStyles from '../../helpers/global.module.css';
import {request} from "../../../services/request";
import {BASE} from "../../../services/endpoints";
import {connect} from "react-redux";
import {IoCloseOutline} from "react-icons/io5";
import classnames from "classnames";
import styles from "../grading.module.css";


class CreateIssueModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoaded: false,
    }

    this.formRef = React.createRef();
  }

  fetchIssues = () => {
    Promise.all([
      request(`/api/courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/graders`)
    ])
      .then(async([res1]) => {
        const users = await res1.json();

        // load submission
        this.setState({
          users: users,
          isLoaded: true,
        });
      })
      .catch(error => {
        console.error(error.message);
      });
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

  onClose = () => {
    this.props.toggleShow();
  }

  onAccept = () => {
    let issue = {
      // target: this.formRef.current.targetSelector.value.split("/")[0],
      reference: this.formRef.current.refSelector.value,
      subject: this.formRef.current.subjectInput.value === "" ? "No title" : this.formRef.current.subjectInput.value,
      description: this.formRef.current.descriptionInput.value,
      addressee: Number(this.formRef.current.addresseeSelector.value),
    }

    this.setState({
      isLoaded: false,
    })

    request(`/api/courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/assessments/${this.props.routeParams.assessmentId}/issues`,
      "POST",
      issue)
      .then(async (response) => {
        let createdIssue = await response.json();

        // save data
        this.props.appendIssue(createdIssue);
        this.props.toggleShow();
      })
  }

  render() {
    return(
      <Modal
        centered
        backdrop="static"
        size="lg"
        onShow={this.fetchIssues}
        show={this.props.show}
        onHide={this.onClose}
        animation={false}
      >
        <div className={globalStyles.modalContainer}>
          <div className={globalStyles.modalHeaderContainer}>
            <h2>Create issue</h2>
            <div className={globalStyles.modalHeaderContainerButton} onClick={this.onClose}>
              <IoCloseOutline size={30}/>
            </div>
          </div>

          {/*<div className={globalStyles.modalDescriptionContainer}>*/}
          {/*  <div>Select projects to import from the list below</div>*/}
          {/*</div>*/}

          {!this.state.isLoaded ?
            <div className={globalStyles.modalSpinnerContainer}>
              <Spinner className={globalStyles.modalSpinner} animation="border" role="status">
                <span className="sr-only">Loading...</span>
              </Spinner>
            </div>
            :
            //body
            <div className={globalStyles.modalBodyContainer}>
              <Form ref={this.formRef} className={styles.gradeEditorContentContainer}>
                {/*<Form.Group controlId="targetSelector" className={styles.gradeEditorCardItem}>*/}
                {/*  <Form.Label>Select target</Form.Label>*/}
                {/*  <Form.Control as="select">*/}
                {/*    <option value={this.props.routeParams.assessmentId +"/2/this assessment"}>this assessment</option>*/}
                {/*    {this.getAllElements(this.props.rubric).map((element) => {*/}
                {/*      return (*/}
                {/*        <option key={element.id} value={element.id +"/"+element.type + "/" + element.name}>{element.name}</option>*/}
                {/*      )*/}
                {/*    })}*/}
                {/*  </Form.Control>*/}
                {/*</Form.Group>*/}

                <Form.Group controlId="refSelector" className={styles.gradeEditorCardItem}>
                  <Form.Label>Select reference issue</Form.Label>
                  <Form.Control as="select">
                    <option value={"null"}>no reference</option>
                    {this.props.issues.map((issue) => {
                      return (
                        <option key={issue.id} value={issue.id}>{issue.subject}</option>
                      )
                    })}
                  </Form.Control>
                </Form.Group>

                <Form.Group controlId="addresseeSelector" className={styles.gradeEditorCardItem}>
                  <Form.Label>Select Addressee</Form.Label>
                  <Form.Control as="select">
                    <option value={"null"}>no addressee</option>
                    {this.state.users.map((grader) => {
                      return (
                        <option key={grader.id} value={grader.id}>{grader.name}</option>
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
              </Form>
              
            </div>
          }

          <div className={globalStyles.modalFooterContainer}>
            <div className={globalStyles.modalFooterContainerButtonGroup}>
              <Button variant="linkLightGray" onClick={this.onClose}>Cancel</Button>
              <Button variant="lightGreen" onClick={this.onAccept}>Create</Button>
            </div>
          </div>
        </div>
      </Modal>
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

export default connect(mapStateToProps, null)(CreateIssueModal)