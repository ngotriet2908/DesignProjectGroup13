import React, {Component} from "react";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {Breadcrumb, Spinner, Button, ListGroup, ListGroupItem, Card, Badge} from "react-bootstrap";
import styles from "../submissionDetails/submissionDetails.module.css";
import globalStyles from "../helpers/global.module.css";
import classnames from "classnames";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {connect} from "react-redux";
import {toast} from "react-toastify";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {IoAdd, IoPencilOutline, IoCloseOutline} from "react-icons/io5";
import Breadcrumbs from "../helpers/Breadcrumbs";
import {URL_PREFIX} from "../../services/config";
import Masonry from 'react-masonry-css'
import {IoArrowForward, IoTrashOutline} from "react-icons/io5";
import {ability, Can, updateAbility} from "../permissions/ProjectAbility";
import { subject } from '@casl/ability';

const masonryBreakpointColumns = {
  default: 2,
  1000: 1,
};

class StudentDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      submissions:[],
      participant: {},
      course: {},
      project: {},
      isLoaded: false,
      isEditing: false,
    }
  }

  deleteHandler = (submission) => {
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${submission.id}/removeParticipant/${this.state.participant.id}?returnAllSubmissions=true`, "DELETE")
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
          submissions: data
        })
      })
      .catch(error => {
        alert(error.message)
      });
  }

  toggleEditing = () => {
    this.setState(prev => {
      return {
        isEditing: !prev.isEditing
      }
    })
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.student);
    Promise.all([
      request(BASE + "courses/" + this.props.match.params.courseId),
      request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId),
      request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/participants/${this.props.match.params.studentId}`)
    ])
      .then(async ([res1, res2, res3]) => {
        const course = await res1.json();
        const project = await res2.json();
        let data = await res3.json();

        if (project.privileges !== null) {
          updateAbility(ability, project.privileges, this.props.user)
          console.log(ability)
          // console.log(ability.can('view',"AdminToolbar"))
          // console.log(ability.can('read',"Submissions"))
        } else {
          console.log("No privileges found.")
        }

        // console.log(data);
        this.setState({
          submissions: data.submissions,
          participant: data.id.user,
          isLoaded: true,
          course: course,
          project: project
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render() {
    if (this.state.isLoading) {
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
          <Breadcrumbs.Item onClick={() => store.dispatch(push(`${URL_PREFIX}/courses/${this.state.course.id}/projects/${this.state.project.id}/participants`))}>
            Participants
          </Breadcrumbs.Item>
          <Breadcrumbs.Item active>
            {this.state.participant.name}
          </Breadcrumbs.Item>
        </Breadcrumbs>

        <div className={classnames(globalStyles.titleContainer, styles.titleContainer)}>
          <h1>{this.state.participant.name}</h1>
        </div>

        <div className={classnames(styles.container)}>
          <Masonry
            breakpointCols={masonryBreakpointColumns}
            className={styles.submissionsMasonryGrid}
            columnClassName={styles.submissionsMasonryColumn}>

            <div className={styles.section}>
              <div className={styles.sectionTitle}>
                <h3 className={styles.sectionTitleH}>Participant Details</h3>
              </div>
              <div className={styles.sectionContent}>

                <Card>
                  <Card.Body>
                    <Card.Title>
                      Participant Info
                    </Card.Title>
                    <h6>Name: {this.state.participant.name}</h6>
                    <h6>sid: {this.state.participant.sNumber}</h6>
                    <h6>email: {this.state.participant.email}</h6>
                  </Card.Body>
                </Card>
              </div>
            </div>

            <div className={styles.section}>
              <div className={classnames(styles.sectionTitle, styles.sectionTitleWithButton)}>
                <h3 className={styles.sectionTitleH}>Submissions</h3>
                {(!this.state.isEditing)?
                  <Button variant="lightGreen" onClick={this.toggleEditing}><IoPencilOutline size={20}/> Edit</Button>
                  :
                  <div className={styles.buttonGroup}>
                    {/*<div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.handleAddParticipant}>*/}
                    {/*  <IoAdd size={26}/>*/}
                    {/*</div>*/}
                    <div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.toggleEditing}>
                      <IoCloseOutline size={26}/>
                    </div>
                  </div>
                }
              </div>
              <div className={styles.sectionContent}>
                <Card>
                  <Card.Body>
                    <Card.Title>
                      Submissions
                    </Card.Title>
                    {this.state.submissions.map((submission) => {
                      return (
                        <Card>
                          <Card.Body>
                            <div className={styles.memberAssessmentHeader}>
                              <h5>
                                {submission.name}
                              </h5>
                              {(this.state.isEditing)?
                                <Can I="edit" this={subject('Submission', (submission.grader === null)? {id: -1}:submission.grader)}>
                                  <div className={classnames(globalStyles.iconButton, styles.dangerButton)}
                                    onClick={() => this.deleteHandler(submission)}>
                                    <IoTrashOutline size={26}/>
                                  </div>
                                </Can>
                                :
                                <div className={classnames(globalStyles.iconButton)}
                                  onClick={() => store.dispatch(push(this.props.match.url.split("/").slice(0, this.props.match.url.split("/").length - 2).join("/") + "/submissions/"+ submission.id))}>
                                  <IoArrowForward size={26}/>
                                </div>
                              }
                            </div>

                            <h6>id: {submission.id}</h6>
                            {/*<h6>name: {submission.name}</h6>*/}
                            <h6>contains current assessment: {submission.containsCurrentAssessment.toString()}</h6>
                          </Card.Body>
                        </Card>
                      )
                    })}
                  </Card.Body>
                </Card>
              </div>
            </div>

          </Masonry>
        </div>
      </div>
    );
  }
}

const mapStateToProps = state => {
  return {
    user: state.users.self
  };
};

const actionCreators = {
  setCurrentLocation
}

export default connect(mapStateToProps, actionCreators)(StudentDetails)