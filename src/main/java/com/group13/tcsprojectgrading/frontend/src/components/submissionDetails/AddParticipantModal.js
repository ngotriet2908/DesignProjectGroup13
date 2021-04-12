import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import globalStyles from "../helpers/global.module.css";
import classnames from "classnames";
import styles from "./submissionDetails.module.css";
import CustomModal from "../helpers/CustomModal";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";


class AddParticipantModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoaded: false,
      students: [],

      selectedStudent: "",
      selectedAssessment: "",
    }
  }

  searchArray(students, student) {
    let i;
    for(i = 0; i < students.length; i++) {
      if (students[i].id === student.id) {
        return true
      }
    }
    return false
  }

  filterCurrentStudents(allStudents, currentStudents) {
    return allStudents.filter((student) => {
      return !this.searchArray(currentStudents, student)
    })
  }

  fetchStudents = () => {
    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/students`)
      .then(async response => {
        let data = await response.json();

        let students = this.filterCurrentStudents(data, this.props.currentStudents);

        this.setState({
          students: students,
          isLoaded: true,
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  onClose = () => {
    this.setState({
      isLoaded: false,
      students: [],
    })

    this.props.toggleShow();
  }

  onAccept = () => {
    if (this.state.selectedStudent == null || this.state.selectedAssessment == null
    || this.state.selectedStudent === "" || this.state.selectedStudent === "") {
      return;
    }

    let studentId = this.state.selectedStudent;
    let assessmentId = this.state.selectedAssessment;

    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/addParticipant/${studentId}/${assessmentId}`,
      "POST")
      .then(async response => {
        let data = await response.json();

        this.setState({
          students: [],
          isLoaded: false,
        })

        this.props.toggleShow();
        // this.props.reloadPage();

        this.props.updateSubmission(data)
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  body = () => {
    return(
      <>
        <FormControl
          variant="outlined"
          className={styles.modalRow}
          fullWidth
        >
          <InputLabel>Student</InputLabel>
          <Select
            labelId="select student"
            id="select student"
            value={this.state.selectedStudent}
            onChange={event => this.setState({
              selectedStudent: event.target.value
            })}
            label="Student"
          >
            <MenuItem value="">
              <em>None</em>
            </MenuItem>
            {this.state.students.map(student => {
              return(
                <MenuItem
                  key={student.id}
                  value={student.id}
                >
                  {student.name}
                </MenuItem>
              )
            })}
          </Select>
        </FormControl>

        <FormControl
          variant="outlined"
          className={styles.modalRow}
          fullWidth
        >
          <InputLabel>Grading sheet</InputLabel>
          <Select
            labelId="select assessment"
            id="select assessment"
            value={this.state.selectedAssessment}
            onChange={event => this.setState({
              selectedAssessment: event.target.value
            })}
            label="Grading sheet"
          >
            <MenuItem value="">
              <em>None</em>
            </MenuItem>
            {this.props.currentAssessments.map(assessment => {
              return(
                <MenuItem
                  key={assessment.id}
                  value={assessment.id}
                >
                  Grading sheet #{assessment.id}
                </MenuItem>
              )
            })}
          </Select>
        </FormControl>
      </>
    )
  }

  render() {
    return(
      <CustomModal
        show={this.props.show}
        onClose={this.props.toggleShow}
        onShow={this.fetchStudents}
        onAccept={this.onAccept}
        title={"Add student"}
        description={"Link a student to the submission"}
        body={this.body()}
        isLoaded={this.state.isLoaded}
      />
    )
  }
}

export default AddParticipantModal