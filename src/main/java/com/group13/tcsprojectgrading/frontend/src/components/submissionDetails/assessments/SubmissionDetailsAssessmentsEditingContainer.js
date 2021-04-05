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
import classnames from 'classnames';
import globalStyles from "../../helpers/global.module.css";
import {IoCloseOutline, IoPencilOutline, IoAdd} from "react-icons/io5";

class SubmissionDetailsAssessmentsEditingContainer extends Component {
  constructor(props) {
    super(props);
  }

  handleCreateNew = (participantId) => {
    let obj = {
      action: "new",
      participantId: participantId,
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

  handleClone = (assessment, participantId) => {
    let obj = {
      action: "clone",
      source: assessment.id,
      participantId: participantId,
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
        if (data.status === 409) {
          alert(data.message)
          return
        }
        this.props.setAssessment(data)
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleActivateCurrent = (assessment, participantId) => {
    let obj = {
      action: "active",
      source: assessment.id,
      participantId: participantId,
    }

    request(`${BASE}courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/submissions/${this.props.params.submissionId}/assessmentManagement`,
      "POST", obj)
      .then(response => {
        return response.json();
      })
      .then(data => {
        if (data.status === 409) {
          alert(data.message)
          return
        }
        this.props.setAssessment(data)
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render() {
    return (
      <div className={classnames(styles.section, styles.assessmentSection)}>
        <div className={classnames(styles.sectionTitle, styles.sectionTitleWithButton)}>
          <h3 className={styles.sectionTitleH}>Grading sheets</h3>
          <div className={styles.buttonGroup}>
            <div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.props.toggleEditing}>
              <IoCloseOutline size={26}/>
            </div>
          </div>
        </div>

        {/*<div className={styles.sectionContent}>*/}
        {/*  <Card>*/}
        {/*    <Card.Body>*/}
        {/*      <ListGroup>*/}
        {/*        {this.props.submission.assessments.map((assessment) => {*/}
        {/*          return (*/}
        {/*            <ListGroupItem id={assessment.id} key={assessment.id}>*/}
        {/*              <SubmissionDetailsAssessmentEditingItemContainer*/}
        {/*                assessments={this.props.submission.assessments}*/}
        {/*                handleMove={this.handleMove}*/}
        {/*                handleClone={() => this.handleClone(assessment)}*/}
        {/*                handleDelete={() => this.handleDelete(assessment)}*/}
        {/*                params={this.props.params}*/}
        {/*                submission={this.props.submission}*/}
        {/*                assessment={assessment}/>*/}
        {/*            </ListGroupItem>)*/}
        {/*        })}*/}
        {/*      </ListGroup>*/}
        {/*    </Card.Body>*/}
        {/*  </Card>*/}
        {/*</div>*/}
        <div className={styles.sectionContent}>
          {this.props.submission.assessments.map((assessment) => {
            return (
              <Card key={assessment.id}>
                <Card.Body>
                  <SubmissionDetailsAssessmentEditingItemContainer
                    assessments={this.props.submission.assessments}
                    handleMove={this.handleMove}
                    handleActivateCurrent={this.handleActivateCurrent}
                    handleCreateNew={this.handleCreateNew}
                    handleClone={this.handleClone}
                    handleDelete={() => this.handleDelete(assessment)}
                    params={this.props.params}
                    submission={this.props.submission}
                    assessment={assessment}/>
                </Card.Body>
              </Card>)
          })}
        </div>

      </div>

    //           <Button style={{display:"inline", marginLeft:"1rem"}}
    //                   onClick={this.handleCreateEmpty}
    //                   variant="success"> Create Empty </Button>
    //           <Button onClick={this.props.toggleEditing} style={{display:"inline", marginLeft:"0.5rem"}}
    //                   variant="danger"> Cancel </Button>
    );
  }
}

export default SubmissionDetailsAssessmentsEditingContainer