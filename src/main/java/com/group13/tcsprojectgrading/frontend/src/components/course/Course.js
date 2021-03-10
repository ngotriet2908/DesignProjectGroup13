import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";

import styles from "./course.module.css";
import spinner from '../helpers/spinner.css'

import { Button, Spinner} from "react-bootstrap";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {URL_PREFIX} from "../../services/config";
import Statistic from "../stat/Statistic";
import EditProjectsModal from "./EditProjectsModal";
import Breadcrumbs from "../helpers/Breadcrumbs";
import ProjectCard from "../home/ProjectCard";
import {IoPencil} from "react-icons/io5";
import StatsCard from "../home/StatsCard";


class Course extends Component {
  constructor (props) {
    super(props)

    this.state = {
      projects: [],
      course: {},
      stats: [],
      isLoaded: false,

      modalEditProjectActiveProjects: [],
      modalEditProjectAvailableProjects: [],
      modalEditProjectsShow: false,
      modalEditShowAlert: false,
      modalEditAlertBody: "",
    }
  }

  componentDidMount() {
    this.reloadPageData();
  }

  reloadPageData = () => {
    Promise.all([
      request(BASE + "courses/" + this.props.match.params.courseId),
      request(`${BASE}courses/${this.props.match.params.courseId}/stats/count`)
    ])
      .then(async([res1, res2]) => {
        const courses = await res1.json();
        const stats = await res2.json();

        this.setState({
          projects: courses.projects,
          course: courses.course,
          stats: stats,
          isLoaded: true,
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  containsObject(obj, list) {
    let i;
    for (i = 0; i < list.length; i++) {
      if (parseInt(list[i].id, 10) === parseInt(obj.id, 10)) {
        return true;
      }
    }
    return false;
  }

  modalEditProjectsHandleShow = () => {
    request(`${BASE}courses/${this.props.match.params.courseId}/addProject/getAllProjects`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        let activeProjects = [...data]
        console.log(activeProjects)

        activeProjects = activeProjects.filter((project) => {
          return this.containsObject(project, [...this.state.projects])
        })

        let availableProjects = [...data]
        console.log(availableProjects)

        availableProjects = availableProjects.filter((project) => {
          return !this.containsObject(project, [...this.state.projects])
        })
        console.log(activeProjects)
        console.log(availableProjects)

        this.setState({
          modalEditProjectActiveProjects: activeProjects,
          modalEditProjectAvailableProjects: availableProjects,
          modalEditProjectsShow: true,
          modalEditShowAlert: false,
          modalEditAlertBody: "",
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  modalEditProjectsHandleClose = () => {
    this.setState({
      modalEditProjectActiveProjects: [],
      modalEditProjectAvailableProjects: [],
      modalEditProjectsShow: false,
      modalEditShowAlert: false,
      modalEditAlertBody: "",
    })
  }

  modalEditProjectsHandleAccept = (event) => {
    // request(BASE + "courses/" + this.props.match.params.courseId,
    request(`${BASE}courses/${this.props.match.params.courseId}/addProject`,
      "POST",
      this.state.modalEditProjectActiveProjects
    )
      .then(() => {
        this.setState({
          modalEditProjectsShow: false,
          modalEditShowAlert: false,
          modalEditAlertBody: "",
        })
        this.reloadPageData();
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  modalEditProjectsHandleActive = (project) => {
    let activeProjects = [...this.state.modalEditProjectActiveProjects]
    activeProjects.push(project)
    let availableProjects = [...this.state.modalEditProjectAvailableProjects]
    availableProjects = availableProjects.filter((project1) => {
      return project1.id !== project.id
    })
    console.log(activeProjects)
    console.log(availableProjects)
    this.setState({
      modalEditProjectActiveProjects: activeProjects,
      modalEditProjectAvailableProjects: availableProjects,
    })
  }

  modalEditProjectsHandleDeactive = (project) => {
    if (project.isVolatile) {
      this.modalEditProjectsHandleShowAlert(`Project ${project.name} is interacted , can't remove project`)
      return
    }

    let availableProjects = [...this.state.modalEditProjectAvailableProjects]
    availableProjects.push(project)
    let activeProjects = [...this.state.modalEditProjectActiveProjects]
    activeProjects = activeProjects.filter((project1) => {
      return project1.id !== project.id
    })
    console.log(activeProjects)
    console.log(availableProjects)
    this.setState({
      modalEditProjectActiveProjects: activeProjects,
      modalEditProjectAvailableProjects: availableProjects,
    })
  }

  modalEditProjectsHandleShowAlert = (body) => {
    this.setState({
      modalEditShowAlert: true,
      modalEditAlertBody: body,
    })
  }

  modalEditProjectsHandleCloseAlert = () => {
    this.setState({
      modalEditShowAlert: false,
      modalEditAlertBody: "",
    })
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
          {[
            {
              name: "Home",
              onClick: () => store.dispatch(push(URL_PREFIX + "/")),
            },
            {
              name: this.state.course.name,
              active: true,
            }
          ]}
        </Breadcrumbs>

        <div className={[styles.sectionTitle, styles.titleContainer].join(" ")}>
          <h2>{this.state.course.name}</h2><span>{(new Date(this.state.course.start_at)).getFullYear()}</span>
        </div>



        <div className={styles.sectionContainer}>
          <div className={[styles.sectionTitle, styles.sectionTitleWithButton].join(" ")}>
            <h3 className={styles.sectionTitleH}>Course projects</h3>

            <div className={styles.sectionTitleButton} onClick={this.modalEditProjectsHandleShow}>
              <IoPencil size={28}/>
            </div>
          </div>

          {this.state.projects.length > 0 ?
            <ul className={styles.ul}>
              {this.state.projects.map(project => {
                return (
                  <li className={styles.li} key={project.id}>
                    <ProjectCard data={project}/>
                  </li>
                )
              })}
            </ul>
            :
            (<div>
              <p>No projects selected in this course. Click on the 'Edit' button to select course projects.</p>
            </div>)
          }
        </div>

        <EditProjectsModal
          show={this.state.modalEditProjectsShow}
          activeProjects={this.state.modalEditProjectActiveProjects}
          availableProjects={this.state.modalEditProjectAvailableProjects}
          onClickDeactive={this.modalEditProjectsHandleDeactive}
          onClickActive={this.modalEditProjectsHandleActive}
          onClose={this.modalEditProjectsHandleClose}
          onAccept={this.modalEditProjectsHandleAccept}

          showAlert={this.state.modalEditShowAlert}
          alertBody={this.state.modalEditAlertBody}
          closeAlertHandle={this.modalEditProjectsHandleCloseAlert}
        />

        <div className={styles.sectionContainer}>
          <div className={[styles.sectionTitle, styles.sectionTitleWithButton].join(" ")}>
            <h3 className={styles.sectionTitleH}>Course statistics</h3>
          </div>

          <ul className={styles.ul}>
            {/*{this.state.stats.map(stat => {*/}
            {/*  return (*/}
            {/*    <li className={styles.li} key={stat.title}>*/}
            {/*      <Statistic title ={stat.title}*/}
            {/*        type={stat.type}*/}
            {/*        data={stat.data}*/}
            {/*        unit={stat.unit}/>*/}
            {/*    </li>*/}
            {/*  );*/}
            {/*})}*/}

            <StatsCard data={this.state.stats}/>

          </ul>
        </div>
      </div>
    )
  }
}

export default Course;