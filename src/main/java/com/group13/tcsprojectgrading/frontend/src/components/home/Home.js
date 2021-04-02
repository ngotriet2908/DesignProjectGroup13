import React, { Component } from 'react'

import styles from './home.module.css'

import {request} from "../../services/request";
import {BASE, USER_COURSES, USER_INFO, USER_TASKS} from "../../services/endpoints";
import {USER_RECENT} from "../../services/endpoints";
import {connect} from "react-redux";
import {saveUserSelf} from "../../redux/user/actions";
import {Spinner} from "react-bootstrap";
import Breadcrumbs from "../helpers/Breadcrumbs";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";
import SectionContainer from "./SectionContainer";
import CourseCard from "./CourseCard";
import ProjectCard from "./ProjectCard";
import HomeTaskCard from "./HomeTaskCard";
import Button from 'react-bootstrap/Button'


import globalStyles from '../helpers/global.module.css';
import {IoCheckboxOutline, IoFileTrayOutline, IoCloudDownloadOutline, IoPencilOutline} from "react-icons/io5";
import {Can} from "../permissions/CoursePageAbility";
import ImportCourseModal from "./ImportCourseModal";

class Home extends Component {
  constructor (props) {
    super(props)
    this.state = {
      courses: [],
      recentProjects: [],
      isLoaded: false,
      tasks: [],
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
      request(BASE + USER_TASKS)
    ])
      .then(async([res1, res2, res3, res4]) => {
        const user = await res1.json();
        const courses = await res2.json();
        const recent = await res3.json();
        const tasks = await res4.json();

        this.props.saveUserSelf(user);

        this.setState({
          recentProjects: recent,
          courses: courses,
          isLoaded: true,
          tasks: tasks
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
          <Breadcrumbs.Item active>Home</Breadcrumbs.Item>
        </Breadcrumbs>

        <div className={globalStyles.titleContainer}>
          <h1>Hi, {this.props.user && this.props.user.name}!</h1>
        </div>

        <div className={styles.container}>
          <SectionContainer
            title={"Your courses"}
            data={this.state.courses}
            emptyText={"You are not participating in any course."}
            Component={CourseCard}
            spreadButton={true}
            button={
              <Button variant="lightGreen" onClick={this.toggleShowImportModal}>
                <IoCloudDownloadOutline size={20}/> Import
              </Button>
            }
            EmptyIcon={IoFileTrayOutline}
          />

          <SectionContainer
            title={"To-Do list"}
            data={this.state.tasks}
            // emptyText={"Your tasks will appear here when they are assigned to you."}
            emptyText={"Nothing to do"}
            Component={HomeTaskCard}
            className={styles.tasksContainer}
            EmptyIcon={IoCheckboxOutline}
          />

          <SectionContainer
            title={"Recent activity"}
            data={this.state.recentProjects}
            emptyText={"Interact with projects to see your recent activity here."}
            Component={ProjectCard}
            EmptyIcon={IoFileTrayOutline}
          />
        </div>

        <ImportCourseModal
          show={this.state.showImportModal}
          toggleShow={this.toggleShowImportModal}
          imported={this.state.courses}
          refresh={this.reloadCourses}
        />
      </div>
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
