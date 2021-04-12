import React, {Component} from "react";
import styles from "../project/project.module.css";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {URL_PREFIX} from "../../services/config";
import {connect} from "react-redux";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {deleteRubric, saveRubric} from "../../redux/rubric/actions";

import {setCurrentCourseAndProject, setCurrentLocation} from "../../redux/navigation/actions";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import Breadcrumbs from "../helpers/Breadcrumbs";

import globalStyles from '../helpers/global.module.css';
import {Can, ability, updateAbility} from "../permissions/ProjectAbility";
import classnames from "classnames";
import * as FileSaver from 'file-saver';
import IssuesProject from "./issues/IssuesProject";
import CircularProgress from "@material-ui/core/CircularProgress";
import StickyHeader from "../helpers/StickyHeader";
import Button from "@material-ui/core/Button";
import SyncIcon from "@material-ui/icons/Sync";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import ImportExportIcon from "@material-ui/icons/ImportExport";
import Grid from "@material-ui/core/Grid";
import PeopleAltIcon from '@material-ui/icons/PeopleAlt';
import BorderColorIcon from '@material-ui/icons/BorderColor';
import DescriptionIcon from '@material-ui/icons/Description';
import FeedbackIcon from '@material-ui/icons/Feedback';
import ListIcon from '@material-ui/icons/List';
import BackupIcon from '@material-ui/icons/Backup';


class Project extends Component {
  constructor(props) {
    super(props)

    this.state = {
      project: {},
      course: {},
      rubric: null,
      grader: {},
      stats: [],
      isLoaded: false,
      issues: [],
      syncing: false
    }
  }

  componentDidMount() {
    const courseId = this.props.match.params.courseId;
    const projectId = this.props.match.params.projectId;

    this.props.setCurrentLocation(LOCATIONS.project);
    this.props.setCurrentCourseAndProject(courseId, projectId);

    Promise.all([
      request(BASE + "courses/" + courseId + "/projects/" + projectId),
      // request(BASE + "courses/" + courseId + "/projects/" + projectId + "/issues"),
    ])
      .then(async([res1, res2]) => {
        const project = await res1.json();
        // const issues = await res2.json();

        this.props.saveRubric(project.rubric);

        if (project.privileges !== null) {
          updateAbility(ability, project.privileges, this.props.user)
          console.log(ability)
        } else {
          console.log("No privileges found.")
        }

        this.setState({
          project: project,
          isLoaded: true,
          syncing: false,
          issues: []
        });
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  componentWillUnmount() {
    this.props.setCurrentCourseAndProject(null, null);
  }

  /*
  Creates a new rubric object and saves it to the store
  */
  onClickCreateRubric = () => {
    let rubric = {
      id: uuidv4(),
      projectId: this.props.match.params.projectId,
      children: []
    }

    request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric", "POST", rubric)
      .then(data => {
        // console.log(data);
        this.props.saveRubric(rubric);
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  onClickRemoveRubric = () => {
    request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric", "DELETE")
      .then(data => {
        // console.log(data);
        this.props.deleteRubric();
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  syncHandler = () => {
    if (this.state.syncing) {
      return;
    }

    this.setState({
      syncing: true
    })

    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/sync`)
      .then(response => {
        if (response.status === 200) {
          this.setState({
            syncing: false
          })
        }
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleExcel = () => {
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/excel`, "GET", null ,"application/octet-stream")
      .then(response => {
        if (response.status === 200) {
          return response.blob()
        }
      })
      .then((data) => {
        const file = new Blob([data], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8'});
        let file_name = this.state.project.name + ", " + Date().toLocaleString();
        FileSaver.saveAs(file, file_name + ".xlsx");

      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleUploadGrades = () => {
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/uploadGrades`, "GET", null ,"application/octet-stream")
      .then(response => {
        if (response.status === 200) {
          return response.blob()
        }
      })
      .then((data) => {
        alert("done")
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render () {
    if (!this.state.isLoaded) {
      return (
        <div className={globalStyles.screenContainer}>
          <CircularProgress className={globalStyles.spinner}/>
        </div>
      )
    }

    return (
      <div className={globalStyles.screenContainer}>
        <Breadcrumbs>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumbs.Item>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.project.course.id ))}>
            {this.state.project.course.name}
          </Breadcrumbs.Item>
          <Breadcrumbs.Item active>
            {this.state.project.name}
          </Breadcrumbs.Item>
        </Breadcrumbs>

        <StickyHeader
          title={this.state.project.name}
          buttons={
            <Can I="sync" a="Submissions">
              <Button
                variant="contained"
                color="primary"
                className={globalStyles.titleActiveButton}
                onClick={this.syncHandler}
                startIcon={<SyncIcon/>}
                disableElevation
              >
                Sync
              </Button>
            </Can>
          }
        />

        <div className={globalStyles.innerScreenContainer}>
          <Grid container spacing={6}>

            <Can I="view" a="AdminToolbar">
              <Grid item xs={6}>
                <div className={classnames(globalStyles.sectionContainer)}>
                  <div className={classnames(globalStyles.sectionTitle, globalStyles.sectionTitleWithButton)}>
                    <h3 className={globalStyles.sectionTitleH}>
                      Administration
                    </h3>
                  </div>

                  <div className={globalStyles.sectionFlexContainer}>
                    <Card className={classnames(styles.card, globalStyles.cardShadow)}>
                      <CardContent
                        className={classnames(styles.cardBody, styles.administrationSectionContainerBody)}
                      >
                        <Button
                          variant="contained"
                          color="primary"
                          onClick={() => store.dispatch(push(this.props.match.url + "/students"))}
                          startIcon={<PeopleAltIcon/>}
                          disableElevation
                        >
                      Students
                        </Button>

                        <Can I="read" a="Submissions">
                          <Button
                            variant="contained"
                            color="primary"
                            onClick={() => store.dispatch(push(this.props.match.url + "/submissions"))}
                            startIcon={<DescriptionIcon/>}
                            disableElevation
                          >
                        Submissions
                          </Button>
                        </Can>

                        <Can I="open" a="ManageGraders">
                          <Button
                            variant="contained"
                            color="primary"
                            onClick={() => store.dispatch(push(this.props.match.url + "/graders"))}
                            startIcon={<BorderColorIcon/>}
                            disableElevation
                          >
                        Graders
                          </Button>
                        </Can>

                        <Can I="open" a="Feedback">
                          <Button
                            variant="contained"
                            color="primary"
                            onClick={() => store.dispatch(push(this.props.match.url + "/feedback"))}
                            startIcon={<FeedbackIcon/>}
                            disableElevation
                          >
                        Feedback
                          </Button>
                        </Can>

                        <Button
                          variant="contained"
                          color="primary"
                          onClick={() => store.dispatch(push(this.props.match.url + "/feedback"))}
                          startIcon={<FeedbackIcon/>}
                          disableElevation
                        >
                          Feedback
                        </Button>

                        <Can I="read" a="Rubric">
                          <Button
                            variant="contained"
                            color="primary"
                            onClick={() => store.dispatch(push(this.props.match.url + "/rubric"))}
                            startIcon={<ListIcon/>}
                            disableElevation
                          >
                        Rubric
                          </Button>
                        </Can>

                        <Button
                          variant="contained"
                          color="primary"
                          onClick={this.handleExcel}
                          startIcon={<ImportExportIcon/>}
                          disableElevation
                        >
                        Export Results
                        </Button>

                        <Button
                          variant="contained"
                          color="primary"
                          onClick={this.handleUploadGrades}
                          startIcon={<BackupIcon/>}
                          disableElevation
                        >
                          Upload Grades To Canvas
                        </Button>
                      </CardContent>
                    </Card>
                  </div>
                </div>
              </Grid>
            </Can>

            {/*<IssuesProject routeMatch={this.props.match} user={this.props.user} issues={this.state.issues}/>*/}
          </Grid>
        </div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
    user: state.users.self
  };
};

const actionCreators = {
  saveRubric,
  deleteRubric,
  setCurrentLocation,
  setCurrentCourseAndProject
}

export default connect(mapStateToProps, actionCreators)(Project)