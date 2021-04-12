import React, {Component} from "react";
import styles from "./submissionDetails.module.css";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import {push} from "connected-react-router";
import globalStyles from "../helpers/global.module.css";
import Breadcrumbs from "../helpers/Breadcrumbs";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";
import classnames from "classnames";
import AddParticipantModal from "./AddParticipantModal";
import {toast} from 'react-toastify'
import LabelRow from "./labels/LabelRow";
import LabelModal from "./labels/LabelModal";
import {ability, Can, updateAbility} from "../permissions/ProjectAbility";
import { subject } from '@casl/ability';
import CircularProgress from "@material-ui/core/CircularProgress";
import StickyHeader from "../helpers/StickyHeader";
import Button from "@material-ui/core/Button";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Grid from "@material-ui/core/Grid";
import Tooltip from "@material-ui/core/Tooltip";
import ScheduleIcon from "@material-ui/icons/Schedule";
import LabelIcon from '@material-ui/icons/Label';
import withTheme from "@material-ui/core/styles/withTheme";
import HourglassEmptyIcon from '@material-ui/icons/HourglassEmpty';
import PersonIcon from '@material-ui/icons/Person';
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import ListItemText from "@material-ui/core/ListItemText";
import List from "@material-ui/core/List";
import Typography from "@material-ui/core/Typography";
import GetAppIcon from '@material-ui/icons/GetApp';
import {IconButton} from "@material-ui/core";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import AddIcon from '@material-ui/icons/Add';
import ErrorOutlineIcon from '@material-ui/icons/ErrorOutline';
import Link from "@material-ui/core/Link";
import CheckCircleIcon from '@material-ui/icons/CheckCircle';
import MoreVertIcon from '@material-ui/icons/MoreVert';
import Pluralize from 'pluralize';


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

        if (project.privileges !== null) {
          updateAbility(ability, project.privileges, this.props.user)
          console.log(ability)
        } else {
          console.log("No privileges found.")
        }

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

  // converts bytes to KB, MB etc.
  // https://stackoverflow.com/questions/15900485/correct-way-to-convert-size-in-bytes-to-kb-mb-gb-in-javascript
  formatBytes(bytes, decimals = 2) {
    if (bytes === 0) return '0 Bytes';

    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];

    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
  }


  render() {
    if (!this.state.isLoaded) {
      return (
        <div className={globalStyles.screenContainer}>
          <CircularProgress className={globalStyles.spinner}/>
        </div>
      )
    }

    return (
      <>
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

        <StickyHeader
          title={this.state.submission.name}
        />

        <div className={globalStyles.innerScreenContainer}>

          {/* labels bar */}
          <LabelRow
            submission={this.state.submission}
            labels={this.state.submission.labels}
            toggleShow={this.toggleShowLabelModal}
          />

          <Grid container className={classnames(styles.container)} spacing={6}>

            {/* left column */}
            <Grid item sm={6} className={styles.section}>
              <div className={globalStyles.sectionTitle}>
                <h2 className={globalStyles.sectionTitleH}>Submission details</h2>
              </div>

              <Card className={classnames(globalStyles.cardShadow, styles.submissionCard)}>
                <CardContent>
                  <div className={styles.submissionCardTitle}>
                    <h4>
                      Submission #{this.state.submission.id}
                    </h4>
                  </div>

                  <div className={styles.cardBody}>
                    <Grid
                      container
                      className={classnames(globalStyles.cardBodyContent)}
                    >
                      <Grid item sm={6}>
                        <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
                          <Tooltip title="Submission name">
                            <LabelIcon style={{color: this.props.theme.palette.primary.main}}/>
                          </Tooltip>
                          <span>{this.state.submission.name}</span>
                        </div>
                      </Grid>

                      <Grid item sm={6}>
                        <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
                          <Tooltip title="Submitted at">
                            <ScheduleIcon style={{color: this.props.theme.palette.primary.main}}/>
                          </Tooltip>
                          <span>
                            {this.state.submission.submittedAt != null
                              ?
                              (new Date(this.state.submission.submittedAt).toISOString().replace('T', ' ').substr(0, 19))
                              :
                              "Unknown"
                            }
                          </span>
                        </div>
                      </Grid>

                      {/*<Grid item sm={6}>*/}
                      {/*  <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>*/}
                      {/*    <Tooltip title="Progress">*/}
                      {/*      <HourglassEmptyIcon style={{color: this.props.theme.palette.primary.main}}/>*/}
                      {/*    </Tooltip>*/}
                      {/*    <span>*/}
                      {/*      /!*{this.state.submission.progress}*!/*/}
                      {/*      85% graded*/}
                      {/*    </span>*/}
                      {/*  </div>*/}
                      {/*</Grid>*/}

                      <Grid item sm={6}>
                        <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
                          <Tooltip title="Grader">
                            <PersonIcon style={{color: this.props.theme.palette.primary.main}}/>
                          </Tooltip>
                          <span>
                            {this.state.submission.grader != null
                              ?
                              this.state.submission.grader.name
                              :
                              "Not assigned"
                            }
                          </span>
                        </div>
                      </Grid>
                    </Grid>
                  </div>
                </CardContent>
              </Card>

              {/* students */}
              <Card className={classnames(globalStyles.cardShadow, styles.submissionCard)}>
                <CardContent>
                  <div className={styles.submissionCardTitleWithButtons}>
                    <h4>
                      Students
                    </h4>

                    <IconButton
                      aria-label="add"
                      onClick={this.toggleShowAddStudentModal}
                      style={{marginRight: "0.2rem"}}
                    >
                      <AddIcon color="primary"/>
                    </IconButton>
                  </div>

                  <List>
                    {this.state.submission.members.map((student) => {
                      return (
                        <ListItem key={student.id} alignItems="flex-start">
                          <ListItemAvatar>
                            <Avatar
                              alt={student.name}
                              src={student.avatar.includes("avatar-50") ? "" : student.avatar}
                            />
                          </ListItemAvatar>

                          <ListItemText
                            primary={
                              student.name
                            }
                            secondary={
                              <>
                                <Typography
                                  component="span"
                                  style={{display: "block"}}
                                  variant="body2"
                                  color="textPrimary"
                                >
                                    s{student.sNumber}
                                </Typography>
                              </>
                            }
                          />

                          <ListItemSecondaryAction>
                            <IconButton edge="end" aria-label="delete" onClick={() => this.deleteParticipantHandler(student)}>
                              <DeleteOutlineIcon style={{color: "red"}}/>
                            </IconButton>
                          </ListItemSecondaryAction>
                        </ListItem>
                      )
                    })}
                  </List>
                </CardContent>
              </Card>

              {/* comments */}
              {this.state.submission.comments.length > 0 &&
              <Card className={classnames(globalStyles.cardShadow, styles.submissionCard)}>
                <CardContent>
                  <div className={styles.submissionCardTitle}>
                    <h4>
                      Comments
                    </h4>
                  </div>

                  <List>
                    {this.state.submission.comments.map((comment) => {
                      return (
                        <ListItem key={comment.id} alignItems="flex-start">
                          <ListItemAvatar>
                            <Avatar
                              alt={comment.author.display_name}
                              src={comment.author.avatar_image_url.includes("avatar-50") ? "" : comment.author.avatar_image_url}
                            />
                          </ListItemAvatar>

                          <ListItemText
                            primary={
                              comment.author.display_name
                            }
                            secondary={
                              <>
                                <Typography
                                  component="span"
                                  style={{display: "block"}}
                                  variant="body2"
                                  color="textPrimary"
                                >
                                  {new Date(comment.created_at).toISOString().replace('T', ' ').substr(0, 19)}
                                </Typography>

                                <Typography
                                  style={{display: "block"}}
                                  component="span"
                                  variant="body2"
                                >
                                  {comment.comment}
                                </Typography>
                              </>
                            }
                          />
                        </ListItem>
                      )
                    })}
                  </List>
                </CardContent>
              </Card>
              }

              {/* attachments */}
              {this.state.submission.attachments.length > 0 &&
              <Card className={classnames(globalStyles.cardShadow, styles.submissionCard)}>
                <CardContent>
                  <div className={styles.submissionCardTitle}>
                    <h4>
                      Attachments
                    </h4>
                  </div>

                  <List
                    className={globalStyles.horizontalList}
                  >
                    {this.state.submission.attachments.map((attachment) => {
                      return (
                        <ListItem key={attachment.id} alignItems="flex-start">
                          <ListItemAvatar>
                            <IconButton onClick={() => window.open(attachment.url)}>
                              <GetAppIcon/>
                            </IconButton>
                          </ListItemAvatar>

                          <ListItemText
                            primary={
                              attachment.display_name
                            }
                            secondary={
                              <>
                                <Typography
                                  component="span"
                                  variant="body2"
                                >
                                  {this.formatBytes(attachment.size)}
                                </Typography>
                              </>
                            }
                          />
                        </ListItem>
                      )
                    })}
                  </List>
                </CardContent>
              </Card>
              }
            </Grid>


            {/* right column */}
            <Grid item sm={6} className={styles.section}>

              {/* assessments */}
              <div className={globalStyles.sectionTitle}>
                <h2 className={globalStyles.sectionTitleH}>Grading sheets</h2>
              </div>

              {this.state.submission.assessments.map((assessment) => {
                return(
                  <Card
                    className={classnames(globalStyles.cardShadow, styles.submissionCard)}
                    key={assessment.id}
                  >
                    <CardContent>
                      <div className={styles.submissionCardTitle}>

                        <Link color="primary" href="#" onClick={(event) => {
                          event.preventDefault();
                          store.dispatch(push("/app/courses/" +
                            this.props.match.params.courseId +
                            "/projects/" +
                            this.props.match.params.projectId +
                            "/submissions/" +
                            this.state.submission.id +
                            "/assessments/" +
                            assessment.id + "/grading"));
                        }}>
                          <h4>
                            Grading sheet #{this.state.submission.id}
                          </h4>
                        </Link>
                      </div>

                      <div className={styles.cardBody}>
                        <Grid
                          container
                          className={classnames(globalStyles.cardBodyContent, styles.cardBodyContent)}
                        >
                          <Grid item sm={6}>
                            <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
                              <Tooltip title="Number of issues">
                                <ErrorOutlineIcon style={{color: this.props.theme.palette.primary.main}}/>
                              </Tooltip>
                              <span>{Pluralize('Issue', assessment.issues.length, true)}</span>
                            </div>
                          </Grid>

                          <Grid item sm={6}>
                            <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
                              <Tooltip title="Progress">
                                <HourglassEmptyIcon style={{color: this.props.theme.palette.primary.main}}/>
                              </Tooltip>
                              <span>
                                {assessment.progress}% graded
                              </span>
                            </div>
                          </Grid>
                        </Grid>

                        <h5>Students</h5>
                        <List>
                          {assessment.members.map((student) => {
                            return (
                              <ListItem key={student.id} alignItems="flex-start">
                                <ListItemAvatar>
                                  <Avatar
                                    alt={student.name}
                                    src={student.avatar.includes("avatar-50") ? "" : student.avatar}
                                  />
                                </ListItemAvatar>

                                <ListItemText
                                  primary={
                                    student.name
                                  }
                                  secondary={
                                    <>
                                      <Typography
                                        component="span"
                                        style={{display: "block"}}
                                        variant="body2"
                                        color="textPrimary"
                                      >
                                        s{student.sNumber}
                                      </Typography>
                                    </>
                                  }
                                />

                                <ListItemSecondaryAction>

                                  {student.isCurrentAssessment &&
                                  <Tooltip title="Is student's active assessment?">
                                    <CheckCircleIcon style={{color: "green"}}/>
                                  </Tooltip>
                                  }

                                  <IconButton
                                    edge="end" aria-label="more"
                                    onClick={() => {}}
                                  >
                                    <MoreVertIcon/>
                                  </IconButton>
                                </ListItemSecondaryAction>
                              </ListItem>
                            )
                          })}
                        </List>

                      </div>
                    </CardContent>
                  </Card>
                )})}

            </Grid>

            {/*      {this.state.submission.assessments != null &&*/}
            {/*    (this.state.isAssessmentEditing ?*/}
            {/*      <SubmissionDetailsAssessmentsEditingContainer*/}
            {/*        setAssessment={this.setAssessment}*/}
            {/*        isEditing={this.state.isAssessmentEditing}*/}
            {/*        toggleEditing={this.toggleEditing}*/}
            {/*        submission={this.state.submission}*/}
            {/*        params={this.props.match.params}*/}
            {/*      />*/}
            {/*      :*/}
            {/*      <SubmissionDetailsAssessmentsContainer*/}
            {/*        isEditing={this.state.isAssessmentEditing}*/}
            {/*        toggleEditing={this.toggleEditing}*/}
            {/*        submission={this.state.submission}*/}
            {/*        params={this.props.match.params}*/}
            {/*      />*/}

          </Grid>

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
        </div>
      </>
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

export default connect(mapStateToProps, actionCreators)(withTheme(SubmissionDetails))