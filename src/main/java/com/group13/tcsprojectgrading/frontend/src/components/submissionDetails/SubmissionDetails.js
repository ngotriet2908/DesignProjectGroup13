import React, {Component} from "react";
import styles from "./submissionDetails.module.css";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {Card, Badge, Breadcrumb,OverlayTrigger, Tooltip, Button, ListGroup, ListGroupItem, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import {push} from "connected-react-router";
import {Link} from "react-router-dom";
import SubmissionDetailsAssessmentsContainer
  from "./SubmissionDetailsAssessmentsContainer/SubmissionDetailsAssessmentsContainer";
import SubmissionDetailsAssessmentsEditingContainer
  from "./SubmissionDetailsAssessmentsContainer/SubmissionDetailsAssessmentsEditingContainer";
import {IoFlagOutline} from "react-icons/io5";
import FlagModal from "./FlagModal";


class SubmissionDetails extends Component {
  constructor(props) {
    super(props);

    // TODO: don't save in state entities that won't be changed
    this.state = {
      course: {},
      project: {},
      submission: {},
      isLoading: true,
      isAssessmentEditing: false,
      flagModalShow: false
    }
  }


  componentDidMount() {
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          submission: data.submission,
          project: data.project,
          course: data.course,
          isLoading: false,
          flagModalShow: false
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  onFlagModalClose = () => {
    this.setState({
      flagModalShow: false
    })
  }

  onFlagModalShow = () => {
    this.setState({
      flagModalShow: true
    })
  }

  updateSubmissionFlags = (data) => {
    let submissionCopy = {...this.state.submission}
    submissionCopy.flags = data
    this.setState({
      submission: submissionCopy
    })
  }
  updateProjectFlags = (data) => {
    let projectCopy = {...this.state.project}
    projectCopy.flags = data
    this.setState({
      project: projectCopy
    })
  }


  setAssessment = (data) => {
    let stateCopy = {...this.state.submission}
    stateCopy.assessments = data
    this.setState({
      submission: stateCopy
    })
  }

  toggleEditing = () => {
    this.setState((prevState) => {
      return {
        isAssessmentEditing: !prevState.isAssessmentEditing
      }
    })
  }

  render() {
    return (
      (this.state.isLoading) ?
        <Spinner className={styles.spinner} animation="border" role="status">
          <span className="sr-only">Loading...</span>
        </Spinner>
        :
        <div className={styles.container}>
          <Breadcrumb>
            <Breadcrumb.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumb.Item>
            <Breadcrumb.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id))}>
              {this.state.course.name}
            </Breadcrumb.Item>
            <Breadcrumb.Item
              onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id + "/projects/" + this.state.project.id))}>
              {this.state.project.name}
            </Breadcrumb.Item>
            <Breadcrumb.Item
              onClick={() => store.dispatch(push(`${URL_PREFIX}/courses/${this.state.course.id}/projects/${this.state.project.id}/submissions`))}>
              Submissions
            </Breadcrumb.Item>
            <Breadcrumb.Item active>
              {this.state.submission.name}
            </Breadcrumb.Item>
          </Breadcrumb>

          <div className={styles.header}>
            <div>
              <h2 style={{display:"inline"}}>{this.state.submission.name}</h2>
              <div style={{float: "right", display:"inline"}}>
                <div className={styles.controlBarButton} onClick={this.onFlagModalShow}>
                  <IoFlagOutline size={26}/>
                </div>
              </div>
              <h5>
                {this.state.submission.flags.map((flag) => {
                  return (
                    <OverlayTrigger
                      placement="bottom"
                      delay={{ show: 250, hide: 400 }}
                      overlay={(props) => (
                        <Tooltip id={flag.id} {...props}>
                          flag description: {flag.description}
                        </Tooltip>)
                      }>
                      <Badge style={{marginRight:"0.5rem"}} variant={flag.variant}>{flag.name}</Badge>
                    </OverlayTrigger>

                  )
                })}
              </h5>
            </div>

          </div>

          <div className={styles.bodyContainer}>
            <div className={styles.infoContainer}>
              <Card>
                <Card.Body>
                  <Card.Title>
                    Submission Info
                  </Card.Title>
                  <Card>
                    <Card.Body>
                      <h6>Id: {this.state.submission.id}</h6>
                      <h6>name: {this.state.submission.name}</h6>
                      <h6>Submitted at: {this.state.submission.submittedAt}</h6>
                      <h6>Progress: {this.state.submission.progress}</h6>
                      {(this.state.submission.grader == null)? null : <h6>Assigned to: {this.state.submission.grader.name}</h6>}
                    </Card.Body>
                  </Card>
                </Card.Body>

              </Card>
            </div>

            <div className={styles.infoContainer}>
              <Card>
                <Card.Body>
                  <Card.Title>
                    Submission Comments
                  </Card.Title>
                  <ListGroup>
                    {this.state.submission.submission_comments.map((comment) => {
                      return (
                        <ListGroupItem key={comment.id}>
                          <div>
                            <h6>Name: {comment.author_name}</h6>
                            <h6>Date: {comment.created_at}</h6>
                            <h6>Comment: {comment.comment}</h6>
                          </div>
                        </ListGroupItem>
                      )
                    })}
                  </ListGroup>
                </Card.Body>

              </Card>
            </div>

            <div className={styles.attachmentContainer}>
              <Card>
                <Card.Body>
                  <Card.Title>
                    Attachments
                  </Card.Title>
                  <ListGroup>
                    {this.state.submission.attachments.map((attachment) => {
                      return (
                        <ListGroupItem key={attachment.id}>
                          <div>
                            <h6>name: {attachment.display_name}</h6>
                            <h6>type: {attachment["content-type"]}</h6>
                            <h6>size: {attachment.size}</h6>
                          </div>
                        </ListGroupItem>)
                    })}
                  </ListGroup>
                </Card.Body>
              </Card>
            </div>
            <div className={styles.discussionContainer}>
              <Card>
                <Card.Body>
                  <Card.Title>
                    Discussion
                  </Card.Title>
                  Working progress
                </Card.Body>
              </Card>
            </div>
            {(this.state.submission.participants == null) ? null :
              <div className={styles.memberContainer}>
                <Card>
                  <Card.Body>
                    <Card.Title>
                      Members
                    </Card.Title>
                    <ListGroup>
                      {this.state.submission.participants.map((member) => {
                        return (
                          <ListGroupItem key={member.sid}>
                            <div className={styles.memberItem}>
                              <h6>name: {member.name}</h6>
                              <h6>sid: {member.sid}</h6>
                              <h6>email: {member.email}</h6>
                            </div>
                          </ListGroupItem>)
                      })}
                    </ListGroup>
                  </Card.Body>
                </Card>
              </div>
            }
            {(this.state.submission.assessments == null) ? null :
              (this.state.isAssessmentEditing)?
                <SubmissionDetailsAssessmentsEditingContainer setAssessment={this.setAssessment} isEditing={this.state.isAssessmentEditing} toggleEditing={this.toggleEditing} submission={this.state.submission} params={this.props.match.params}/> :
                <SubmissionDetailsAssessmentsContainer isEditing={this.state.isAssessmentEditing} toggleEditing={this.toggleEditing} submission={this.state.submission} params={this.props.match.params}/>
            }
          </div>
          <FlagModal show={this.state.flagModalShow}
                     onClose = {this.onFlagModalClose}
                     flags = {this.state.submission.flags}
                     availableFlags = {this.state.project.flags}
                     updateSubmissionFlags = {this.updateSubmissionFlags}
                     updateProjectFlags = {this.updateProjectFlags}
                     params={this.props.match.params}
          />
        </div>
    );
  }
}

export default SubmissionDetails