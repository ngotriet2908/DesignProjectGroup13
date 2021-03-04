import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import styles from "./course.module.css";
import ProjectCard from "./ProjectCard";
import {Breadcrumb} from "react-bootstrap";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {URL_PREFIX} from "../../services/config";
import testStats from "../stat/testStats.json";
import Statistic from "../stat/Statistic";


class Course extends Component {
  constructor (props) {
    super(props)
    this.state = {
      projects: [],
      course: {},
      stats: [],
      loaded: false,
    }
  }

  async componentDidMount() {
    console.log("Course mounted.")
    // console.log(this.props)

    try {
      let response = await request(BASE + "courses/" + this.props.match.params.courseId);
      let data = await response.json();
      console.log(data);
      this.setState({
        projects: data.projects,
        course: data.course
      });
      response = await request(`${BASE}stats/courses/${this.props.match.params.courseId}/count`);
      data = await response.json();
      this.setState({ stats: data })
    } catch (error) {
      console.log(error);
    }
  }
  render () {
    return (
      <div className={styles.container}>
        <Breadcrumb>
          <Breadcrumb.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumb.Item>
          <Breadcrumb.Item active>
            {this.state.course.name}
          </Breadcrumb.Item>
        </Breadcrumb>

        <div className={styles.titleContainer}>
          <h2>{this.state.course.name}</h2>
        </div>

        <div className={styles.overviewContainer}>
          <h3 className={styles.sectionTitle}>Overview/Stats</h3>
          <div>
            <p>Blablabla here...</p>
            <p>Some people like Sponge Bob while some are in love with anime. @Y (NB: me.interests.contains(anime) == false)</p>
          </div>
        </div>

        <div className={styles.projectsContainer}>
          <h3>Course projects</h3>
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
            {/*{testStats.map(stat => {*/}
            {/*  return (*/}
            {/*    <li className={styles.li} key={stat.title}>*/}
            {/*      <Statistic name={stat.title}*/}
            {/*                 type={stat.type}*/}
            {/*                 data={stat.data}*/}
            {/*                 unit={stat.unit}/>*/}
            {/*    </li>*/}
            {/*  );*/}
            {/*})}*/}

            {this.state.stats.map(stat => {
              return (
                <li className={styles.li} key={stat.title}>
                  <Statistic name={stat.title}
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