import {Button, Spinner, Modal} from 'react-bootstrap'
import React, {Component} from "react";
import globalStyles from '../helpers/global.module.css';
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {connect} from "react-redux";
import {IoCloseOutline, IoCheckboxOutline, IoSquareOutline} from "react-icons/io5";
import classnames from "classnames";
import ButtonTooltip from "../helpers/ButtonTooltip";


class ImportProjectsModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      projects: [],
      isLoaded: false,

      selected: []
    }
  }

  fetchProjects = () => {
    // console.log(this.props.imported);

    request(`${BASE}courses/${this.props.currentCourse.id}/projects/all`)
      .then(async(response) => {
        let projects = await response.json();

        this.setState({
          isLoaded: true,
          projects: projects
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  toggleSelected = (project) => {
    // project is selected
    if (this.state.selected.find((selectedProject) => selectedProject.id === project.id)) {
      this.setState(prevState => ({
        selected: prevState.selected.filter((selectedProject) => selectedProject.id !== project.id)
      }))
    } else {
      // project is not selected
      this.setState(prevState => ({
        selected: [...prevState.selected, project]
      }))
    }
  }

  onClose = () => {
    this.props.toggleShow();
  }

  onAccept = () => {
    let body = this.state.selected.map((selectedProject) => {
      console.log(selectedProject);

      return {
        id: selectedProject.id,
      }
    })

    request(
      `${BASE}courses/${this.props.currentCourse.id}/projects`,
      "POST",
      body
    )
      .then((response) => {
        // console.log(response);
        this.props.toggleShow();
        this.props.refresh();
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render() {
    return(
      <Modal
        centered
        backdrop="static"
        size="lg"
        onShow={this.fetchProjects}
        show={this.props.show}
        onHide={this.onClose}
        animation={false}
      >
        <div className={globalStyles.modalContainer}>
          <div className={globalStyles.modalHeaderContainer}>
            <h2>Import Projects</h2>
            <ButtonTooltip className={classnames(globalStyles.modalHeaderContainerButton, globalStyles.modalHeaderContainerCloseButton)}
                           content="Close" placement="top" onClick={this.onClose}>
              <IoCloseOutline size={30}/>
            </ButtonTooltip>
          </div>

          <div className={globalStyles.modalDescriptionContainer}>
            <div>Select projects to import from the list below</div>
          </div>

          {!this.state.isLoaded ?
            <div className={globalStyles.modalSpinnerContainer}>
              <Spinner className={globalStyles.modalSpinner} animation="border" role="status">
                <span className="sr-only">Loading...</span>
              </Spinner>
            </div>
            :
            //body
            <div className={globalStyles.modalBodyContainer}>
              {/* courses */}
              {(this.state.projects.length === 0 ||
                this.state.projects.filter((course) => {
                  return !this.props.imported.find((importedProject) => importedProject.id == course.id);
                }).length === 0) ?
                <div className={classnames(globalStyles.modalBodyContainerRow, globalStyles.modalBodyContainerRowEmpty)}>
                  No courses available for import
                </div>
                :
                (this.state.projects
                  .filter((project) => {
                    return !this.props.imported.find((importedProject) => importedProject.id == project.id);
                  })
                  .map((project) => {
                    const selected = this.state.selected.find((selectedProject) => selectedProject.id == project.id);

                    return (
                      <div
                        className={classnames(globalStyles.modalBodyContainerRow, selected && globalStyles.modalBodyContainerRowActive)}
                        key={project.id}
                        onClick={
                          () => this.toggleSelected(project)
                        }>
                        {selected ?
                          <IoCheckboxOutline size={16}/>
                          :
                          <IoSquareOutline size={16}/>
                        }
                        <span>{project.name}</span>
                      </div>
                    )
                  })
                )
              }
            </div>
          }

          <div className={globalStyles.modalFooterContainer}>
            <div className={globalStyles.modalFooterContainerButtonGroup}>
              <Button variant="linkLightGray" onClick={this.onClose}>Cancel</Button>
              <Button variant="lightGreen" onClick={this.onAccept}>Import</Button>
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

export default connect(mapStateToProps, null)(ImportProjectsModal)