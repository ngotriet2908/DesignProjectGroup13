import React, { Component } from 'react'

import styles from './home.module.css'
import spinner from '../helpers/spinner.css'

import {request} from "../../services/request";
import {BASE, USER_COURSES, USER_INFO, USER_TASKS} from "../../services/endpoints";
import {USER_RECENT} from "../../services/endpoints";
import {connect} from "react-redux";
import {saveUser} from "../../redux/user/actions";
import {Spinner} from "react-bootstrap";
import Breadcrumbs from "../helpers/Breadcrumbs";
import HomeTasksContainer from "./HomeTasksContainer";
import HomeCoursesContainer from "./HomeCoursesContainer";
import HomeRecentContainer from "./HomeRecentContainer";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {LOCATIONS} from "../../redux/navigation/reducers/navigation";

class Home extends Component {
  constructor (props) {
    super(props)
    this.state = {
      courses: [],
      recentProjects: [],
      isLoaded: false,
      tasks: []
    }
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

        this.props.saveUser(user);

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

  render () {
    if (!this.state.isLoaded) {
      return(
        <div className={styles.container}>
          <Spinner className={spinner.spinner} animation="border" role="status">
            <span className="sr-only">Loading...</span>
          </Spinner>
        </div>
      )
    }

    return (
      <div className={styles.container}>
        <Breadcrumbs>
          {[{
            name: "Home",
            active: true,
          }]}
        </Breadcrumbs>

        <div className={styles.coursesContainer}>
          <h1>Hi, {this.props.user && this.props.user.name}!</h1>
        </div>

        <HomeCoursesContainer courses={this.state.courses}/>
        <HomeTasksContainer tasks={this.state.tasks}/>
        <HomeRecentContainer recentProjects={this.state.recentProjects}/>
      </div>
    )
  }
}

const actionCreators = {
  saveUser,
  setCurrentLocation
}

const mapStateToProps = state => {
  return {
    user: state.user.user
  };
};

export default connect(mapStateToProps, actionCreators)(Home)
