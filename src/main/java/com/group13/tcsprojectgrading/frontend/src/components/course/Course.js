import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE, COURSE_INFO, USER_COURSES} from "../../services/endpoints";
import styles from "./course.module.css";
import ProjectCard from "./ProjectCard";
import {Button} from "react-bootstrap";


class Course extends Component {
  constructor (props) {
    super(props)
    this.state = {
      projects: [],
      course: {},
      loaded: false,
    }
  }

  componentDidMount() {
    console.log("Course mounted.")
    // console.log(this.props)

    request(`${BASE}${USER_COURSES}/${COURSE_INFO}/${this.props.match.params.course_id}`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          projects: data.projects,
          course: data.course
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }
  render () {
    return (
      <div className={styles.container}>
        <div className={styles.headers}>
          <h1>{this.state.course.name}</h1>
        </div>
        <div className={styles.overviewContainer}>
          <h2>Course overview</h2>
        </div>

        <div className={styles.projectsContainer}>
          <h2>Course projects</h2>
          <ul className={styles.ul}>
            {this.state.projects.map(project => {
              console.log(project)
              return (
                <li className={styles.li} key={project.uuid}>
                  <ProjectCard data={project}/>
                </li>
              )
            })}
          </ul>
        </div>

      </div>
    )

  }
}



export default Course