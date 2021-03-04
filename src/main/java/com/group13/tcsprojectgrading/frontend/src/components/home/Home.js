import React, { Component } from 'react'
import CourseCard from './CourseCard'

import styles from './home.module.css'
import {request} from "../../services/request";
import {BASE, USER_COURSES, USER_INFO} from "../../services/endpoints";
import {USER_RECENT} from "../../services/endpoints";
import ProjectCard from "../course/ProjectCard";
import {connect} from "react-redux";
import {saveUser} from "../../redux/user/actions";
import Breadcrumb from "react-bootstrap/Breadcrumb";

class Home extends Component {
  constructor (props) {
    super(props)
    this.state = {
      courses: [],
      recentProjects: [],
      loaded: false,
    }
  }

  componentDidMount () {
    request(BASE + USER_COURSES)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          courses: data
        })
      })
      .catch(error => {
        console.error(error.message);
      });

    request(BASE + USER_INFO)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.props.saveUser(data);
      })
      .catch(error => {
        console.error(error.message)
      });

    request(BASE + USER_RECENT)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          recentProjects: data
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render () {
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
        </div>
      </div>
    )
  }
}

const actionCreators = {
  saveUser,
}

export default connect(null, actionCreators)(Home)
