import React, { Component } from 'react'
import CourseCard from './CourseCard'

import styles from './home.module.css'
import {request} from "../../services/request";
import {BASE, USER_COURSES, USER_INFO, USER_TASKS} from "../../services/endpoints";
import {USER_RECENT} from "../../services/endpoints";
import ProjectCard from "../course/ProjectCard";
import {connect} from "react-redux";
import {saveUser} from "../../redux/user/actions";
import Breadcrumb from "react-bootstrap/Breadcrumb";
import {Spinner} from "react-bootstrap";
import HomeTaskCard from "./HomeTaskCard";

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
          <Spinner className={styles.spinner} animation="border" role="status">
            <span className="sr-only">Loading...</span>
          </Spinner>
        </div>
      )
    }

    return (
      <div>
        <div className={styles.container}>
          <Breadcrumb>
            <Breadcrumb.Item active>Home</Breadcrumb.Item>
          </Breadcrumb>

          <div className={styles.coursesContainer}>
            <h2>My Courses</h2>
            <ul className={styles.ul}>
              {this.state.courses.map(course => {
                return (
                  <li className={styles.li} key={course.uuid}>
                    <CourseCard data={course}/>
                  </li>
                )
              })}
            </ul>
          </div>

          <div className={styles.recentContainer}>
            <h2>Recent Activity</h2>
            <div>
              {this.state.recentProjects.length > 0 ?
                (<ul className={styles.ul}>
                  {this.state.recentProjects.map(project => {
                    return (
                      <li className={styles.li} key={project.id}>
                        <ProjectCard data={project}/>
                      </li>
                    )
                  })}
                </ul>)
                :
                (<div>
                  No recent activity
                </div>)
              }
            </div>
          </div>

          <div className={styles.homeTasksContainer}>
            <h2>My Tasks</h2>
            <ul className={styles.ul}>
              {this.state.tasks.map(task => {
                return (
                  <li className={styles.li} key={task.project.id +"/"+task.course.id}>
                    <HomeTaskCard task={task}/>
                  </li>
                )
              })}
            </ul>
          </div>

        </div>
      </div>
    )
  }
}

const actionCreators = {
  saveUser,
}

export default connect(null, actionCreators)(Home)
