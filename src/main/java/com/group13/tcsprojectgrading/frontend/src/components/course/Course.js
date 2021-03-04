import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import styles from "./course.module.css";
import ProjectCard from "./ProjectCard";
import {Breadcrumb, Button} from "react-bootstrap";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {URL_PREFIX} from "../../services/config";


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
          {/*<div className={styles.projectsToolBar}>*/}
            <h3>Course projects</h3>
            {/*<Button className={styles.projectsToolBarButton}*/}
            {/*        variant="primary"*/}
            {/*        onClick={this.modalEditProjectsHandleShow}>*/}
            {/*  edit projects*/}
            {/*</Button>*/}
          {/*</div>*/}
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


        {/*<EditProjectsModal*/}
        {/*  show={this.state.modalEditGradersShow}*/}
        {/*  activeProjects={this.state.modalEditGradersActiveGraders}*/}
        {/*  availableProjects={this.state.modalEditGradersAvailableGraders}*/}
        {/*  onClickDeactive={this.modalEditGradersHandleDeactive}*/}
        {/*  onClickActive={this.modalEditGradersHandleActive}*/}
        {/*  onClose={this.modalEditGradersHandleClose}*/}
        {/*  onAccept={this.modalEditGradersHandleAccept}*/}

        {/*  showAlert={this.state.modalEditShowAlert}*/}
        {/*  alertBody={this.state.modalEditAlertBody}*/}
        {/*  closeAlertHandle={this.modalEditGradersHandleCloseAlert}*/}
        {/*/>*/}

      </div>
    )
  }
}

export default Course