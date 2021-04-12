import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";

import styles from "./course.module.css";

import store from "../../redux/store";
import {push} from "connected-react-router";
import {URL_PREFIX} from "../../services/config";
import EditProjectsModal from "./ImportProjectsModal";
import Breadcrumbs from "../helpers/Breadcrumbs";
import ProjectCard from "./ProjectCard";
import {Can, ability, updateAbilityCoursePage} from "../permissions/CoursePageAbility";
import {connect} from "react-redux";
import {deleteCurrentCourse, saveCurrentCourse} from "../../redux/courses/actions";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import {setCurrentLocation} from "../../redux/navigation/actions";

import globalStyles from '../helpers/global.module.css';
import classnames from 'classnames';
import CircularProgress from "@material-ui/core/CircularProgress";
import StickyHeader from "../helpers/StickyHeader";
import Button from "@material-ui/core/Button";
import ImportExportIcon from "@material-ui/icons/ImportExport";

import SyncIcon from '@material-ui/icons/Sync';
import Grid from "@material-ui/core/Grid";
import EmptyCourseCard from "../home/EmptyCourseCard";


class Course extends Component {
  constructor (props) {
    super(props)

    this.state = {
      course: {},
      stats: [],
      isLoaded: false,
      user: {},

      syncing: false,

      showModal: false,
    }
  }

  componentDidMount() {
    this.props.setCurrentLocation(LOCATIONS.course);
    this.reloadPageData();
  }

  reloadPageData = () => {
    Promise.all([
      request(BASE + "courses/" + this.props.match.params.courseId),
      // request(`${BASE}courses/${this.props.match.params.courseId}/stats/count`)
    ])
      .then(async([res1, res2]) => {
        const course = await res1.json();

        updateAbilityCoursePage(ability, course.role)
        this.props.saveCurrentCourse(course);

        course.projects.forEach((project, index, array) => {
          array[index].course = {
            id: course.id,
            name: course.name
          }
        })

        this.setState({
          course: course,
          // stats: stats,
          isLoaded: true,
          syncing: false
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  componentWillUnmount () {
    this.props.deleteCurrentCourse();
  }

  toggleShowModal = () => {
    this.setState(prevState => ({
      showModal: !prevState.showModal
    }))
  }

  syncCourseHandler = () => {
    if (this.state.syncing) {
      return;
    }

    this.setState({
      syncing: true
    })

    request(`${BASE}courses/${this.props.match.params.courseId}/sync`, "POST", {})
      .then(response => {
        if (response.status === 200) {
          // console.log("Sync successful");
          this.reloadPageData()
        }
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  reloadProjects = () => {
    this.setState({
      isLoaded: false,
    })

    request(BASE + "courses/" + this.props.match.params.courseId)
      .then(async response => {
        const course = await response.json();

        updateAbilityCoursePage(ability, course.role)
        this.props.saveCurrentCourse(course);

        course.projects.forEach((project, index, array) => {
          array[index].course = {
            id: course.id,
            name: course.name
          }
        })

        this.setState({
          course: course,
          isLoaded: true,
        })
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
      <>
        <Breadcrumbs>
          <Breadcrumbs.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumbs.Item>
          <Breadcrumbs.Item active>{this.state.course.name}</Breadcrumbs.Item>
        </Breadcrumbs>

        <StickyHeader
          title={this.state.course.name}
          buttons={
            <Can I="sync" a="Course">
              <Button
                variant="contained"
                color="primary"
                className={globalStyles.titleActiveButton}
                onClick={this.syncCourseHandler}
                startIcon={<SyncIcon/>}
                disableElevation
              >
                Sync
              </Button>
            </Can>
          }
        />

        <div className={globalStyles.innerScreenContainer}>
          {/* projects */}
          <div className={classnames(globalStyles.sectionContainer)}>
            <div className={classnames(globalStyles.sectionTitle, globalStyles.sectionTitleWithButtonSpread)}>
              <h2 className={globalStyles.sectionTitleH}>Projects</h2>

              <Can I="write" a="Projects">
                <Button
                  variant="contained"
                  color="primary"
                  onClick={this.toggleShowModal}
                  startIcon={<ImportExportIcon/>}
                  disableElevation
                >
                Import
                </Button>
              </Can>

            </div>

            <Grid container spacing={3}>
              {this.state.course.projects.map((course, index) => (
                <Grid key={index} item sm={6} lg={4}>
                  <ProjectCard data={course}/>
                </Grid>
              ))}
              {this.state.course.projects.length === 0 &&
              <Grid item sm={6} lg={4}>
                <EmptyCourseCard
                  action={this.toggleShowModal}
                  description={"Import project"}
                  className={styles.projectCard}
                />
              </Grid>
              }
            </Grid>
          </div>

          <EditProjectsModal
            show={this.state.showModal}
            toggleShow={this.toggleShowModal}
            refresh={this.reloadProjects}
            imported={this.state.course.projects}
          />

        </div>
      </>
    )
  }
}

const actionCreators = {
  saveCurrentCourse,
  deleteCurrentCourse,
  setCurrentLocation
}

export default connect(null, actionCreators)(Course)