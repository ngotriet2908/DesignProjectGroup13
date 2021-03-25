import React, {Component} from "react";
import styles from "./submissionDetails.module.css";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {Card, Badge,OverlayTrigger, Tooltip, Button, ListGroup, ListGroupItem, Spinner} from "react-bootstrap";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import {push} from "connected-react-router";
import SubmissionDetailsAssessmentsContainer
  from "./SubmissionDetailsAssessmentsContainer/SubmissionDetailsAssessmentsContainer";
import SubmissionDetailsAssessmentsEditingContainer
  from "./SubmissionDetailsAssessmentsContainer/SubmissionDetailsAssessmentsEditingContainer";
import {IoFlagOutline,IoAdd,  IoSyncOutline} from "react-icons/io5";
import FlagModal from "./FlagModal";
import globalStyles from "../helpers/global.module.css";
import Breadcrumbs from "../helpers/Breadcrumbs";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {deleteCurrentCourse, saveCurrentCourse} from "../../redux/courses/actions";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";
import classnames from "classnames";


class SubmissionDetails extends Component {
  constructor(props) {
    super(props);

    // TODO: don't save in state entities that won't be changed
    this.state = {
      course: {},
      project: {},
      submission: {},
      isLoaded: false,
      isAssessmentEditing: false,
      flagModalShow: false
    }
  }


  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.submission);

    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}`)
      .then(async response => {
        let data = await response.json();
        // console.log(data);
        this.setState({
          submission: data.submission,
          project: data.project,
          course: data.course,
          isLoaded: true,
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
    if (!this.state.isLoaded) {
      return(
        <div className={globalStyles.container}>
          <Spinner className={globalStyles.spinner} animation="border" role="status">
            <span className="sr-only">Loading...</span>
          </Spinner>
        </div>
      )
    }

    return (
      <div className={globalStyles.container}>
        <Breadcrumbs>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id))}>
            {this.state.course.name}
          </Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id + "/projects/" + this.state.project.id))}>
            {this.state.project.name}
          </Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(`${URL_PREFIX}/courses/${this.state.course.id}/projects/${this.state.project.id}/submissions`))}>
            Submissions
          </Breadcrumbs.Item>
          <Breadcrumbs.Item active>
            {this.state.submission.name}
          </Breadcrumbs.Item>
        </Breadcrumbs>

        <div className={classnames(globalStyles.titleContainer, styles.titleContainer)}>
          <h1>{this.state.submission.name}</h1>
        </div>

        <div className={styles.labelsContainer}>
          {this.state.submission.flags.map((flag) => {
            return (
              <OverlayTrigger
                key={flag.id}
                placement="bottom"
                delay={{ show: 250, hide: 400 }}
                overlay={(props) => (
                  <Tooltip id={flag.id} {...props}>
                    {flag.description}
                  </Tooltip>)
                }>
                <Badge className={globalStyles.label} variant={flag.variant}>{flag.name}</Badge>
              </OverlayTrigger>

            )
          })}

          <Button variant="dashed" onClick={this.onFlagModalShow}>
            <IoAdd/> Add label
          </Button>
        </div>

        <div className={classnames(styles.container)}>

          <div className={styles.section}>
            <div className={styles.sectionTitle}>
              <h3 className={styles.sectionTitleH}>Submission Info</h3>
            </div>

            <div className={styles.sectionContent}>
              <Card>
                <Card.Body>
                  <h6>Id: {this.state.submission.id}</h6>
                  <h6>name: {this.state.submission.name}</h6>
                  <h6>Submitted at: {this.state.submission.submittedAt}</h6>
                  <h6>Progress: {this.state.submission.progress}</h6>
                  {(this.state.submission.grader == null)? null : <h6>Assigned to: {this.state.submission.grader.name}</h6>}
                </Card.Body>
              </Card>
            </div>
          </div>

          <div className={styles.section}>
            <div className={styles.sectionTitle}>
              <h3 className={styles.sectionTitleH}>Submission Comments</h3>
            </div>

            <div className={styles.sectionContent}>
              <Card>
                <Card.Body>
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
          </div>



          <div className={styles.section}>
            <div className={styles.sectionTitle}>
              <h3 className={styles.sectionTitleH}>Attachments</h3>
            </div>

            <div className={styles.sectionContent}>
              <Card>
                <Card.Body>
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
          </div>


          {/*<div className={styles.discussionContainer}>*/}
          {/*  <Card>*/}
          {/*    <Card.Body>*/}
          {/*      <Card.Title>*/}
          {/*          Discussion*/}
          {/*      </Card.Title>*/}
          {/*        Working progress*/}
          {/*    </Card.Body>*/}
          {/*  </Card>*/}
          {/*</div>*/}


          {(this.state.submission.participants != null) &&
            <div className={styles.section}>
              <div className={styles.sectionTitle}>
                <h3 className={styles.sectionTitleH}>Members</h3>
              </div>

              <div className={styles.sectionContent}>
                <Card>
                  <Card.Body>
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
            </div>
          }


          {(this.state.submission.assessments != null) &&
            (this.state.isAssessmentEditing) ?
            <SubmissionDetailsAssessmentsEditingContainer
              setAssessment={this.setAssessment}
              isEditing={this.state.isAssessmentEditing}
              toggleEditing={this.toggleEditing}
              submission={this.state.submission}
              params={this.props.match.params}
            />
            :
            <SubmissionDetailsAssessmentsContainer
              isEditing={this.state.isAssessmentEditing}
              toggleEditing={this.toggleEditing}
              submission={this.state.submission}
              params={this.props.match.params}
            />
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

const actionCreators = {
  setCurrentLocation
}

export default connect(null, actionCreators)(SubmissionDetails)