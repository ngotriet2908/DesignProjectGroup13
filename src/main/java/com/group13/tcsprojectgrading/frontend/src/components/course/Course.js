import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import styles from "./course.module.css";
import ProjectCard from "./ProjectCard";
import testStats from "../stat/testStats.json";
import Statistic from "../stat/Statistic";


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

    request(BASE + "courses/" + this.props.match.params.courseId)
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
          <div>Blablabla here...
            Some people like Sponge Bob while some adore anime. @Yevhen (NB: me.interests.contains(anime) == false)</div>
        </div>

        <div className={styles.projectsContainer}>
          <h2>Course projects</h2>
          <ul className={styles.ul}>
            {this.state.projects.map(project => {
              console.log(project)
              return (
                <li className={styles.li} key={project.id}>
                  <ProjectCard data={project}/>
                </li>
              )
            })}
          </ul>
        </div>

        <div className={styles.statsContainer}>
          <h2>Course statistics</h2>
          <ul className={styles.ul}>
            {testStats.map(stat => {
              return (
                <li className={styles.li} key={stat.name}>
                  <Statistic name={stat.name}
                             type={stat.type}
                             data={stat.data}
                             unit={stat.unit}/>
                </li>
              );
            })}
          </ul>
        </div>

      </div>
    )

  }
}



export default Course;