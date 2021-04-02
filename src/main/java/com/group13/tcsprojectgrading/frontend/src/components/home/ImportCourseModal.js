import {Button, Modal, Form, Card, ListGroup, ListGroupItem, Alert, Spinner} from 'react-bootstrap'
import React, {Component} from "react";
import styles from "./home.module.css";
import globalStyles from '../helpers/global.module.css';
import {IoCheckboxOutline, IoCloseOutline, IoSquareOutline} from "react-icons/io5";
import classnames from "classnames";
import {request} from "../../services/request";
import {BASE, PROJECT, USER_COURSES} from "../../services/endpoints";
import {isTeacher} from "../permissions/functions";

class ImportCourseModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      courses: [],
      isLoaded: false,
      selected: []
    }
  }

  fetchCourses = () => {
    request(`${BASE}courses/all`)
      .then(async response => {
        let courses = await response.json();
        // console.log(courses);
        this.setState({
          courses: courses,
          isLoaded: true
        })

      })
      .catch(error => {
        console.error(error.message);
      });
  }

  toggleSelected = (course) => {
    // course is selected
    if (this.state.selected.find((selectedCourse) => selectedCourse.id === course.id)) {
      this.setState(prevState => ({
        selected: prevState.selected.filter((selectedCourse) => selectedCourse.id !== course.id)
      }))
    } else {
      // course is not selected
      this.setState(prevState => ({
        selected: [...prevState.selected, course]
      }))
    }
  }

  onClose = () => {
    this.props.toggleShow();
  }

  onAccept = () => {
    let body = this.state.selected.map((selectedCourse) => {
      // console.log(selectedCourse);
      return {
        id: selectedCourse.id,
        name: selectedCourse.name,
        start_at: selectedCourse.start_at
      }
    })

    request(`${BASE}courses/`, "POST", body)
      .then(async response => {
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
        onShow={this.fetchCourses}
        show={this.props.show}
        onHide={this.onClose}
        animation={false}
      >
        <div className={globalStyles.modalContainer}>
          <div className={globalStyles.modalHeaderContainer}>
            <h2>Import Courses</h2>
            <div className={globalStyles.modalHeaderContainerButton} onClick={this.onClose}>
              <IoCloseOutline size={30}/>
            </div>
          </div>

          <div className={globalStyles.modalDescriptionContainer}>
            <div>
              Select courses to import from the list below
            </div>
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
              {(this.state.courses.length === 0 ||
                this.state.courses.filter((course) => {
                  return !this.props.imported.find((importedCourse) => importedCourse.id == course.id);
                }).length === 0) ?
                <div className={classnames(globalStyles.modalBodyContainerRow, globalStyles.modalBodyContainerRowEmpty)}>
                  No courses available for import
                </div>
                :
                (this.state.courses
                  .filter((course) => {
                    return !this.props.imported.find((importedCourse) => importedCourse.id == course.id);
                  })
                  .map((course) => {
                    const selected = this.state.selected.find((selectedCourse) => selectedCourse.id == course.id);

                    return (
                      <div
                        className={classnames(globalStyles.modalBodyContainerRow, selected && globalStyles.modalBodyContainerRowActive)}
                        key={course.id}
                        onClick={
                          () => this.toggleSelected(course)
                        }>
                        {selected ?
                          <IoCheckboxOutline size={16}/>
                          :
                          <IoSquareOutline size={16}/>
                        }
                        <span>{course.name}</span>
                      </div>
                    )
                  })
                )
              }
            </div>
          }

          {/* footer */}
          <div className={globalStyles.modalFooterContainer}>
            <div className={globalStyles.modalFooterContainerButtonGroup}>
              <Button variant="linkLightGray" onClick={this.onClose}>
                Cancel
              </Button>

              <Button variant="lightGreen"
                onClick={this.onAccept}>
                Import
              </Button>
            </div>
          </div>
        </div>
      </Modal>
    )
  }
}

export default ImportCourseModal;