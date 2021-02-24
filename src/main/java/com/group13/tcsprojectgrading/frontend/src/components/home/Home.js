import React, { Component } from 'react'
import CourseCard from './CourseCard'

import styles from './home.module.css'
import {request} from "../../services/request";
import {BASE, USER_COURSES, USER_INFO} from "../../services/endpoints";
// import ProjectCard from '../projects/ProjectCard'

class Home extends Component {
  constructor (props) {
    super(props)
    this.state = {
      courses: [],
      user: {},
      // recentProjects: recentProjects,
      loaded: false,
    }
  }

  componentDidMount () {
    console.log("Home mounted.")

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
        this.setState({
          user: data
        })
      })
      .catch(error => {
        console.error(error.message)
      });
  }

  render () {
    return (
      <div className={styles.container}>
        <h1>Welcome, {this.state.user.name}!</h1>
        <div className={styles.coursesContainer}>
          <h2>Your courses</h2>
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

        {/*<div className={styles.recentContainer}>*/}
        {/*    <h2>Recent projects</h2>*/}
        {/*    <div>*/}
        {/*        <ul className={styles.ul}>*/}
        {/*            {this.state.recentProjects.map(project => {*/}
        {/*                return (*/}
        {/*                    <li className={styles.li} key={project.id}>*/}
        {/*                        <ProjectCard data={project}/>*/}
        {/*                    </li>*/}
        {/*                )*/}
        {/*            })}*/}
        {/*        </ul>*/}
        {/*    </div>*/}
        {/*</div>*/}
      </div>
    )
  }
}

export default Home
