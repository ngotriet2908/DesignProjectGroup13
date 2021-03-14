import {Button, Spinner, Modal, Alert} from 'react-bootstrap'
import React, {Component} from "react";
import styles from "./course.module.css";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {connect} from "react-redux";
import {IoCloseOutline, IoCheckboxOutline, IoSquareOutline} from "react-icons/io5";


class EditProjectsModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoaded: false,

      active: this.props.currentActive,
      showAlert: false,
      alertBody: "",

      all: [],
    }
  }

  componentDidMount() {
    request(`${BASE}courses/${this.props.currentCourse.id}/projects`)
      .then(async(response) => {
        let projects = await response.json();

        this.setState({
          isLoaded: true,
          // all: projects.concat(projects).concat(projects).concat(projects)
          all: projects
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  // TODO: chaos with ID types
  findById = (project, list) => {
    return list.find((element) => {
      return element.id == project.id;
    })
  }

  close = () => {
    this.props.setShow(false);
  }

  accept = () => {
    request(`${BASE}courses/${this.props.currentCourse.id}/projects-active`, "POST", this.state.active
    )
      .then(() => {
        this.props.setShow(false, true);
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleActivate = (project) => {
    let activeProjects = [...this.state.active]
    activeProjects.push(project)

    this.setState({
      active: activeProjects,
    })
  }

  handleDeactivate = (project) => {
    // TODO: Projects that were modified in the app cannot be deactivated. Gray background for such projects.
    if (project.isVolatile) {
      this.showAlert(`Cannot deactivate '${project.name}' because it was already edited in the app.`)
      return;
    }

    let activeProjects = [...this.state.active]
    activeProjects = activeProjects.filter((project1) => {
      return project1.id != project.id
    })

    this.setState({
      active: activeProjects,
    })
  }

  handleChangeStatus = (project) => {
    if (this.findById(project, this.state.active)) {
      console.log("Deactivate");
      this.handleDeactivate(project);
    } else {
      console.log("Activate");
      this.handleActivate(project);
    }
  }

  showAlert = (body) => {
    this.setState({
      showAlert: true,
      alertBody: body,
    })
  }

  hideAlert = () => {
    this.setState({
      showAlert: false,
      alertBody: "",
    })
  }

  render() {
    return(
      <Modal
        centered
        backdrop="static"
        size="lg"
        show={this.props.show}
        onHide={this.close}
        animation={false}
      >
        <div className={styles.modalContainer}>
          <div className={styles.modalHeaderContainer}>
            <h2>Select Projects</h2>
            <div className={styles.modalHeaderContainerButton} onClick={this.close}>
              <IoCloseOutline size={30}/>
            </div>
          </div>

          {this.state.showAlert &&
          <Alert variant="custom" onClose={this.hideAlert} show={this.state.showAlert}>
            <p>
              {this.state.alertBody}
            </p>
          </Alert>
          }

          <div className={styles.modalDescriptionContainer}>
            <div>Select from the list below the projects that you want to add to the application.</div>
          </div>

          <div className={styles.modalBodyContainer}>
            {this.state.all.map(project => {
              let projectFound = this.findById(project, this.state.active);

              return (
                <div
                  key={project.id}
                  className={
                    [styles.modalBodyContainerRow, (projectFound ? styles.modalBodyContainerRowActive : [])
                    ].join(' ')}
                  onClick={this.handleChangeStatus.bind(this, project)}
                >
                  {projectFound ?
                    <IoCheckboxOutline size={16}/>
                    :
                    <IoSquareOutline size={16}/>
                  }
                  <span>{project.name}</span>
                </div>
              )
            })}
          </div>

          <div className={styles.modalFooterContainer}>
            <div className={styles.modalFooterContainerButtonGroup}>
              <Button variant="linkLightGray" onClick={this.close}>Cancel</Button>
              <Button variant="lightGreen" onClick={this.accept}>Save</Button>
            </div>
          </div>
        </div>
      </Modal>
    )
  }
}

const mapStateToProps = state => {
  return {
    currentCourse: state.courses.currentCourse
  };
};

export default connect(mapStateToProps, null)(EditProjectsModal)