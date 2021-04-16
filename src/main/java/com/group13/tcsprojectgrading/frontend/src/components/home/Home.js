import React, { Component } from 'react'

import styles from './home.module.css'

import {request} from "../../services/request";
import {BASE, USER_COURSES, USER_INFO, USER_ISSUES, USER_TASKS} from "../../services/endpoints";
import {USER_RECENT} from "../../services/endpoints";
import {connect} from "react-redux";
import {saveUserSelf} from "../../redux/user/actions";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import CourseCard from "./CourseCard";
import globalStyles from '../helpers/global.module.css';

import ImportCoursesModal from "./ImportCoursesModal";
import StickyHeader from "../helpers/StickyHeader";

import CircularProgress from "@material-ui/core/CircularProgress";
import Button from "@material-ui/core/Button";
import Breadcrumbs from "../helpers/Breadcrumbs";
import classnames from "classnames";
import Grid from "@material-ui/core/Grid";
import ImportExportIcon from '@material-ui/icons/ImportExport';
import EmptyCourseCard from "./EmptyCourseCard";
import TaskPanel from "./TaskPanel";


class Home extends Component {
  constructor (props) {
    super(props)
    this.state = {
      courses: [],
      recentProjects: [],
      isLoaded: false,

      tasks: [],
      issues: [],

      showImportModal: false
    }
  }

  toggleShowImportModal = () => {
    this.setState(prevState=> ({
      showImportModal: !prevState.showImportModal
    }))
  }

  componentDidMount () {
    this.props.setCurrentLocation(LOCATIONS.home);

    Promise.all([
      request(BASE + USER_INFO),
      request(BASE + USER_COURSES),
      request(BASE + USER_RECENT),
      request(BASE + USER_TASKS),
      request(BASE + USER_ISSUES)
    ])
      .then(async([res1, res2, res3, res4, res5]) => {
        const user = await res1.json();
        let courses = await res2.json();
        const recent = await res3.json();
        const tasks = await res4.json();
        const issues = await res5.json();

        this.props.saveUserSelf(user);

        this.setState({
          recentProjects: recent,
          courses: courses,
          isLoaded: true,
          tasks: tasks,
          issues: issues
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  reloadCourses = () => {
    this.setState({
      isLoaded: false,
    })

    request(BASE + USER_COURSES)
      .then(async response => {
        let courses = await response.json();
        // console.log(courses);
        this.setState({
          courses: courses,
          isLoaded: true
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
          <Breadcrumbs.Item active>Home</Breadcrumbs.Item>
        </Breadcrumbs>

        <StickyHeader title={`Hi, ${this.props.user && this.props.user.name}!`}/>

        <div className={globalStyles.innerScreenContainer}>
          {/* left column */}
          <Grid container spacing={8}>
            <Grid item xs={7} className={styles.leftColumn}>

              <div className={classnames(globalStyles.sectionContainer)}>
                <div className={classnames(globalStyles.sectionTitle, globalStyles.sectionTitleWithButtonSpread)}>

                  <h2 className={globalStyles.sectionTitleH}>Courses</h2>

                  <Button
                    variant="contained"
                    color="primary"
                    onClick={this.toggleShowImportModal}
                    startIcon={<ImportExportIcon/>}
                    disableElevation
                  >
                  Import
                  </Button>
                </div>

                <Grid container spacing={3}>
                  {this.state.courses.map((course, index) => (
                    <Grid key={index} item sm={6} lg={6}>
                      <CourseCard data={course}/>
                    </Grid>
                  ))}

                  {this.state.courses.length === 0 &&
                  <Grid item sm={6} lg={6}>
                    <EmptyCourseCard
                      action={this.toggleShowImportModal}
                      description={"Import course"}
                      className={styles.courseCard}
                    />
                  </Grid>
                  }
                </Grid>
              </div>
            </Grid>

            <Grid item xs={5}>
              <TaskPanel
                tasks={this.state.tasks}
                issues={this.state.issues}
              />
            </Grid>
          </Grid>
        </div>

        <ImportCoursesModal
          show={this.state.showImportModal}
          toggleShow={this.toggleShowImportModal}
          imported={this.state.courses}
          refresh={this.reloadCourses}
        />
      </>
    )
  }
}

const actionCreators = {
  saveUserSelf,
  setCurrentLocation
}

const mapStateToProps = state => {
  return {
    user: state.users.self
  };
};

export default connect(mapStateToProps, actionCreators)(Home)
