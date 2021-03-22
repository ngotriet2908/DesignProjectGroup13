import React, {Component} from "react";
import styles from "../submissionDetails.module.css";
import {request} from "../../../services/request";
import {BASE} from "../../../services/endpoints";
import {Card, Breadcrumb, Button, ListGroup, ListGroupItem, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import store from "../../../redux/store";
import {URL_PREFIX} from "../../../services/config";
import {push} from "connected-react-router";
import {Link} from "react-router-dom";
import SubmissionDetailsAssessmentItemContainer from "./SubmissionDetailsAssessmentItemContainer";
import SubmissionDetailsAssessmentEditingItemContainer from "./SubmissionDetailsAssessmentEditingItemContainer";


class SubmissionDetailsAssessmentsEditingContainer extends Component {
  constructor(props) {
    super(props);

  }

  handleCreateEmpty = () => {
    let obj = {
      action: "new",
    }

    request(`${BASE}courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/submissions/${this.props.params.submissionId}/assessmentManagement`,
      "POST", obj)
      .then(response => {
        return response.json();
      })
      .then(data => {
        this.props.setAssessment(data)
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleMove = (srcAssessment, desAssessment, participant) => {
    let obj = {
      action: "move",
      source: srcAssessment,
      destination: desAssessment,
      participantId: participant,
    }

    request(`${BASE}courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/submissions/${this.props.params.submissionId}/assessmentManagement`,
      "POST", obj)
      .then(response => {
        return response.json();
      })
      .then(data => {
        this.props.setAssessment(data)
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleClone = (assessment) => {
    let obj = {
      action: "clone",
      source: assessment.id
    }

    request(`${BASE}courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/submissions/${this.props.params.submissionId}/assessmentManagement`,
      "POST", obj)
      .then(response => {
        return response.json();
      })
      .then(data => {
        this.props.setAssessment(data)
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleDelete = (assessment) => {
    let obj = {
      action: "delete",
      source: assessment.id
    }

    request(`${BASE}courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/submissions/${this.props.params.submissionId}/assessmentManagement`,
      "POST", obj)
      .then(response => {
        return response.json();
      })
      .then(data => {
        this.props.setAssessment(data)
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render() {
    return (
      <div className={styles.memberContainer}>
        <Card>
          <Card.Body>
            <Card.Title>
              <div style={{display:"inline"}}>
                <h5 style={{display:"inline"}}>Assessments</h5>
                <Button style={{display:"inline", marginLeft:"1rem"}}
                        onClick={this.handleCreateEmpty}
                        variant="success"> Create Empty </Button>
                <Button onClick={this.props.toggleEditing} style={{display:"inline", marginLeft:"0.5rem"}}
                        variant="danger"> Cancel </Button>
              </div>
            </Card.Title>
            <ListGroup>
              {this.props.submission.assessments.map((assessment) => {
                return (
                  <ListGroupItem id={assessment.id}>
                    <SubmissionDetailsAssessmentEditingItemContainer
                      assessments={this.props.submission.assessments}
                      handleMove={this.handleMove}
                      handleClone={() => this.handleClone(assessment)}
                      handleDelete={() => this.handleDelete(assessment)}
                      params={this.props.params}
                      submission={this.props.submission}
                      assessment={assessment}/>
                  </ListGroupItem>)
              })}
            </ListGroup>
          </Card.Body>
        </Card>
      </div>
    );
  }
}

export default SubmissionDetailsAssessmentsEditingContainer