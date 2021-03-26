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
import {IoFlagOutline,IoAdd,  IoSyncOutline, IoPencilOutline, IoCloseOutline} from "react-icons/io5";
import FlagModal from "./FlagModal";
import globalStyles from "../helpers/global.module.css";
import Breadcrumbs from "../helpers/Breadcrumbs";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {deleteCurrentCourse, saveCurrentCourse} from "../../redux/courses/actions";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";
import classnames from "classnames";
import AddParticipantModal from "./AddParticipantModal";
import ParticipantItemEdit from "./ParticipantItemEdit";
import ParticipantItem from "./ParticipantItem";
import {toast} from 'react-toastify'

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
      isAddingParticipant: false,
      flagModalShow: false,
      participantModalShow: false,
      allParticipants: []
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
          flagModalShow: false,
          participantModalShow: false,
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  deleteParticipantHandler = (member) => {
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}/removeParticipant/${member.id}?returnAllSubmissions=false`, "DELETE")
      .then(response => {
        return response.json()
      })
      .then(data => {
        if (data.hasOwnProperty("error")) {
          console.log(data.status)
          console.log(data.message)
          // alert(data.message)
          toast.error(data.message, {
            position: "top-center",
            autoClose: 5000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
          });
          return
        }
        this.setState({
          submission: data
        })
      })
      .catch(error => {
        alert(error.message)
      });
  }

  toggleAddingParticipant = () => {
    this.setState(prev => {
      return {isAddingParticipant: !prev.isAddingParticipant}
    })
  }

  searchArray(participants, participant) {
    let i;
    for(i = 0; i < participants.length; i++) {
      if (participants[i].id === participant.id) return true
    }
    return false
  }

  filterCurrentParticipant(participants) {
    return participants.filter((participant) => {
      return !this.searchArray(this.state.submission.participants, participant)
    })
  }

  updateSubmission = (submission) => {
    console.log("update submission")
    console.log(submission)
    this.setState({
      submission: submission,
      participantModalShow: false
    })
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

  onAddParticipantModalClose = () => {
    this.setState({
      participantModalShow: false,
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

  handleAddParticipant = () => {
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/participants`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          allParticipants: data.participants,
          participantModalShow: true
        })
      })
      .catch(error => {
        console.error(error.message);
      });
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
          <Button variant="dashed" onClick={this.onFlagModalShow}>
            <IoAdd/> Add label
          </Button>

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
              <div className={classnames(styles.sectionTitle, styles.sectionTitleWithButton)}>
                <h3 className={styles.sectionTitleH}>Participants</h3>
                {(!this.state.isAddingParticipant)?
                  <div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.toggleAddingParticipant}>
                    <IoPencilOutline size={26}/>
                  </div>
                  :
                  <div className={styles.buttonGroup}>
                    <div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.handleAddParticipant}>
                    <IoAdd size={26}/>
                    </div>
                    <div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.toggleAddingParticipant}>
                    <IoCloseOutline size={26}/>
                    </div>
                  </div>
                }


              </div>

              <div className={styles.sectionContent}>
                <Card>
                  <Card.Body>
                    <ListGroup>
                      {this.state.submission.participants.map((member) => {
                        return ((this.state.isAddingParticipant)?
                            <ParticipantItemEdit member={member} handleDelete={() => this.deleteParticipantHandler(member)}/>
                            :
                            <ParticipantItem member={member}/>
                          )
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

        <AddParticipantModal
          show={this.state.participantModalShow}
          onClose={this.onAddParticipantModalClose}
          participants={this.filterCurrentParticipant(this.state.allParticipants)}
          assessments={this.state.submission.assessments}
          params={this.props.match.params}
          updateSubmission={this.updateSubmission}
        />
      </div>
    );
  }
}

const actionCreators = {
  setCurrentLocation
}

export default connect(null, actionCreators)(SubmissionDetails)