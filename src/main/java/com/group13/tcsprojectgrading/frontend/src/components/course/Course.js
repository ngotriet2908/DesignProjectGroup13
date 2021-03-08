import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import styles from "./course.module.css";
import ProjectCard from "./ProjectCard";
import {Breadcrumb, Button} from "react-bootstrap";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {URL_PREFIX} from "../../services/config";
import Statistic from "../stat/Statistic";
import EditProjectsModal from "./EditProjectsModal";


class Course extends Component {
  constructor (props) {
    super(props)
    this.state = {
      projects: [],
      course: {},
      stats: [],
      loaded: false,


      modalEditProjectActiveProjects: [],
      modalEditProjectAvailableProjects: [],
      modalEditProjectsShow: false,
      modalEditShowAlert: false,
      modalEditAlertBody: "",
    }
  }

  componentDidMount() {
    console.log("Course mounted.")
    // console.log(this.props)

    this.reloadPageData()
  }

  reloadPageData = () => {
    request(BASE + "courses/" + this.props.match.params.courseId)
      .then(response => {
        return response.json();
      })
      .then(data => {
        // console.log(data);
        this.setState({
          projects: data.projects,
          course: data.course
        })
      })
      .catch(error => {
        console.error(error.message);
      });

    request(`${BASE}stats/courses/${this.props.match.params.courseId}/count`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        this.setState({
          stats: data
        })
      })
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
            <p>And more blablabla: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi mollis consectetur elit ut sagittis. Aenean sit amet tempor enim, et finibus nisi. Phasellus imperdiet molestie blandit. </p>
          </div>
        </div>

        <div className={styles.projectsContainer}>
          <div className={styles.projectsToolBar}>
            <h3 className={styles.projectsToolBarText}>Course projects</h3>
            <Button className={styles.projectsToolBarButton}
              variant="primary"
              onClick={this.modalEditProjectsHandleShow}>
              edit projects
            </Button>
          </div>
          <ul className={styles.ul}>
            {this.state.projects.map(project => {
              return (
                <li className={styles.li} key={project.id}>
                  <ProjectCard data={project}/>
                </li>
              )
            })}
          </ul>
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