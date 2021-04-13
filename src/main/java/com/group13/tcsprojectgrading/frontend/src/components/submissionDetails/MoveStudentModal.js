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


class MoveStudentModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoaded: true,

      selectedAssessment: "",
    }
  }

  onClose = () => {
    this.setState({
      selectedAssessment: "",
    })

    this.props.toggleShow();
  }

  onAccept = () => {
    if (this.state.selectedAssessment == null || this.state.selectedAssessment === "") {
      return;
    }

    let obj = {
      action: "move",
      source: this.props.currentAssessment.id,
      destination: this.state.selectedAssessment,
      participantId: this.props.currentStudent.id
    }

    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/assessmentManagement`,
      "POST", obj)
      .then(async response => {
        let data = await response.json();
        this.props.setAssessments(data);

        this.onClose();
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
          <InputLabel>Grading sheet</InputLabel>
          <Select
            value={this.state.selectedAssessment}
            onChange={event => this.setState({
              selectedAssessment: event.target.value
            })}
            label="Grading sheet"
          >
            <MenuItem value="">
              <em>None</em>
            </MenuItem>
            {this.props.assessments.map((assessment) => {
              return(
                (this.props.currentAssessment != null && assessment.id !== this.props.currentAssessment.id) ?
                  <MenuItem
                    key={assessment.id}
                    value={assessment.id}
                  >
                    <span>Grading sheet #{assessment.id} </span>
                  </MenuItem>
                  :
                  null
              )})
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
        onClose={this.onClose}
        onAccept={this.onAccept}
        title={"Move student"}
        description={"Move the student to another grading sheet."}
        body={this.body()}
        isLoaded={this.state.isLoaded}
      />
    )
  }
}

export default MoveStudentModal