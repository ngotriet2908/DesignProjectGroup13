import React, {Component} from "react";
import styles from "./submissionDetails.module.css";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {Card, ListGroup, Spinner} from "react-bootstrap";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import {push} from "connected-react-router";
import SubmissionDetailsAssessmentsContainer
  from "./assessments/SubmissionDetailsAssessmentsContainer";
import SubmissionDetailsAssessmentsEditingContainer
  from "./assessments/SubmissionDetailsAssessmentsEditingContainer";
import {IoAdd, IoPencilOutline, IoCloseOutline} from "react-icons/io5";
import globalStyles from "../helpers/global.module.css";
import Breadcrumbs from "../helpers/Breadcrumbs";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";
import classnames from "classnames";
import AddParticipantModal from "./AddParticipantModal";
import ParticipantItemEdit from "./ParticipantItemEdit";
import ParticipantItem from "./ParticipantItem";
import {toast} from 'react-toastify'
import Masonry from 'react-masonry-css'
import LabelRow from "./labels/LabelRow";
import LabelModal from "./labels/LabelModal";
import Button from "react-bootstrap/Button";

const masonryBreakpointColumns = {
  default: 2,
  1000: 1,
};

class SubmissionDetails extends Component {
  constructor(props) {
    super(props);

    this.state = {
      course: {},
      project: {},
      submission: {},

      isLoaded: false,
      isAssessmentEditing: false,
      isAddingParticipant: false,

      // participantModalShow: false,

      showLabelModal: false,

      showAddStudentModal: false,
    }
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.submission);

    Promise.all([
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}`),
      request(BASE + "courses/" + this.props.match.params.courseId),
      request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId),
    ])
      .then(async([res1, res2, res3]) => {
        const submission = await res1.json();
        const course = await res2.json();
        const project = await res3.json();

        this.setState({
          course: course,
          project: project,
          submission: submission,
          isLoaded: true,
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

  updateSubmission = (submission) => {
    this.setState({
      submission: submission,
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


  // Label modal

  toggleShowLabelModal = () => {
    this.setState(prevState => ({
      showLabelModal: !prevState.showLabelModal
    }))
  }

  replaceLabels = (labels) => {
    this.setState(prevState => ({
      submission: {
        ...prevState.submission,
        labels: labels
      }
    }))
  }

  // Add student modal
  toggleShowAddStudentModal = () => {
    this.setState(prevState => ({
      showAddStudentModal: !prevState.showAddStudentModal
    }))
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

        {/* labels bar */}
        <LabelRow
          labels={this.state.submission.labels}
          toggleShow={this.toggleShowLabelModal}
        />

        <div className={classnames(styles.container)}>
          <Masonry
            breakpointCols={masonryBreakpointColumns}
            className={styles.submissionsMasonryGrid}
            columnClassName={styles.submissionsMasonryColumn}>

            <div className={styles.section}>
              <div className={styles.sectionTitle}>
                <h3 className={styles.sectionTitleH}>Submission Details</h3>
              </div>

              <div className={styles.sectionContent}>
                <Card>
                  <Card.Body>
                    <h6>Id: {this.state.submission.id}</h6>
                    <h6>name: {this.state.submission.name}</h6>
                    <h6>Submitted at: {this.state.submission.submittedAt}</h6>
                    {/*<h6>Progress: {this.state.submission.progress}</h6>*/}
                    {this.state.submission.grader != null && <h6>Assigned to: {this.state.submission.grader.name}</h6>}
                  </Card.Body>
                </Card>
              </div>
            </div>

            {this.state.submission.assessments != null &&
            (this.state.isAssessmentEditing ?
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
            )
            }

            {this.state.submission.comments.length > 0 &&
            <div className={styles.section}>
              <div className={styles.sectionTitle}>
                <h3 className={styles.sectionTitleH}>Submission Comments</h3>
              </div>

              <div className={styles.sectionContent}>
                {this.state.submission.comments.map((comment) => {
                  return (
                    <Card key={comment.id}>
                      <Card.Body>
                        <div>
                          <h6>Name: {comment.author_name}</h6>
                          <h6>Date: {comment.created_at}</h6>
                          <h6>Comment: {comment.comment}</h6>
                        </div>
                      </Card.Body>
                    </Card>
                  )
                })}
              </div>
            </div>
            }

            {this.state.submission.attachments.length > 0 &&
            <div className={styles.section}>
              <div className={styles.sectionTitle}>
                <h3 className={styles.sectionTitleH}>Attachments</h3>
              </div>

              <div className={styles.sectionContent}>
                {this.state.submission.attachments.map((attachment) => {
                  return (
                    <Card key={attachment.id}>
                      <Card.Body>
                        <div>
                          <h6>name: {attachment.display_name}</h6>
                          <h6>type: {attachment["content-type"]}</h6>
                          <h6>size: {attachment.size}</h6>
                        </div>
                      </Card.Body>
                    </Card>
                  )
                })}
              </div>
            </div>
            }

            {(this.state.submission.members != null) &&
            <div className={styles.section}>
              <div className={classnames(styles.sectionTitle, styles.sectionTitleWithButton)}>
                <h3 className={styles.sectionTitleH}>Students</h3>
                {(!this.state.isAddingParticipant)?
                  <Button variant="lightGreen" onClick={this.toggleAddingParticipant}><IoPencilOutline size={20}/> Edit</Button>
                  :
                  <div className={styles.buttonGroup}>
                    <div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.toggleShowAddStudentModal}>
                      <IoAdd size={26}/>
                    </div>
                    <div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.toggleAddingParticipant}>
                      <IoCloseOutline size={26}/>
                    </div>
                  </div>
                }
              </div>

              <div className={styles.sectionContent}>
                {this.state.submission.members.map((member) => {
                  return (
                    <Card key={member.id}>
                      <Card.Body>
                        {(this.state.isAddingParticipant) ?
                          <ParticipantItemEdit key={member.id} member={member}
                            handleDelete={() => this.deleteParticipantHandler(member)}/>
                          :
                          <ParticipantItem key={member.id} member={member}/>
                        }
                      </Card.Body>
                    </Card>
                  )
                })}
              </div>
            </div>
            }
          </Masonry>
        </div>

        <LabelModal
          show={this.state.showLabelModal}
          toggleShow={this.toggleShowLabelModal}
          routeParams={this.props.match.params}
          currentLabels={this.state.submission.labels}
          replaceLabels={this.replaceLabels}
        />

        <AddParticipantModal
          show={this.state.showAddStudentModal}
          toggleShow={this.toggleShowAddStudentModal}
          routeParams={this.props.match.params}
          currentStudents={this.state.submission.members}
          currentAssessments={this.state.submission.assessments}
          updateSubmission={this.updateSubmission}
        />

        {/*<AddParticipantModal*/}
        {/*  show={this.state.participantModalShow}*/}
        {/*  onClose={this.onAddParticipantModalClose}*/}
        {/*  participants={this.filterCurrentParticipant(this.state.students)}*/}
        {/*  assessments={this.state.submission.assessments}*/}
        {/*  params={this.props.match.params}*/}
        {/*  updateSubmission={this.updateSubmission}*/}
        {/*/>*/}
      </div>
    );
  }
}

const actionCreators = {
  setCurrentLocation
}

export default connect(null, actionCreators)(SubmissionDetails)