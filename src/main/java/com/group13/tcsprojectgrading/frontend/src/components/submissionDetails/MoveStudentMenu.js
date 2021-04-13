import React, {Component} from "react";
import {IconButton} from "@material-ui/core";
import MoreVertIcon from "@material-ui/icons/MoreVert";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";


class MoveStudentMenu extends Component {
  constructor(props) {
    super(props);

    this.state = {
      studentMenuAnchorElement: null,
    }
  }

  handleStudentMenuClose = () => {
    this.setState({
      studentMenuAnchorElement: null
    })
  };

  handleStudentMenuOpen = (event) => {
    this.setState({
      studentMenuAnchorElement: event.currentTarget,
    })
  }

  handleCreateNew = (participantId) => {
    let obj = {
      action: "new",
      participantId: participantId,
    }

    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/assessmentManagement`,
      "POST", obj)
      .then(async response => {
        let data = await response.json();
        this.props.setAssessments(data)
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleClone = (assessment, participantId) => {
    let obj = {
      action: "clone",
      source: assessment.id,
      participantId: participantId,
    }

    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/assessmentManagement`,
      "POST", obj)
      .then(async response => {
        let data = await response.json();
        this.props.setAssessments(data)
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleDelete = (assessment) => {
    let obj = {
      action: "delete",
      source: assessment.id
    }

    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/assessmentManagement`,
      "POST", obj)
      .then(async response => {
        let data = await response.json();

        if (data.status === 409) {
          alert(data.message)
          return
        }

        this.props.setAssessments(data)
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  handleActivateCurrent = (assessment, participantId) => {
    let obj = {
      action: "active",
      source: assessment.id,
      participantId: participantId,
    }

    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/assessmentManagement`,
      "POST", obj)
      .then(async response => {
        let data = await response.json();

        if (data.status === 409) {
          // alert(data.message)
          return
        }

        this.props.setAssessments(data)
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render() {
    return(
      <>
        <IconButton
          edge="end" aria-label="more"
          onClick={this.handleStudentMenuOpen}>
          <MoreVertIcon/>
        </IconButton>

        <Menu
          anchorEl={this.state.studentMenuAnchorElement}
          keepMounted
          open={Boolean(this.state.studentMenuAnchorElement)}
          onClose={this.handleStudentMenuClose}
        >
          {[!this.props.student.isCurrentAssessment && (
            <MenuItem
              key={"makeActive"}
              onClick={() => {
                this.handleStudentMenuClose()
                this.handleActivateCurrent(this.props.assessment, this.props.student.id)
              }}
            >
            Make active
            </MenuItem>
          ),

          this.props.assessment.members.length > 1 && (
            <MenuItem
              key={"newSheet"}
              onClick={() => {
                this.handleStudentMenuClose()
                this.handleCreateNew(this.props.student.id)
              }}
            >
            Create new empty sheet
            </MenuItem>
          ),

          this.props.assessment.members.length > 1 && (
            <MenuItem
              key={"copySheet"}
              onClick={() => {
                this.handleStudentMenuClose()
                this.handleClone(this.props.assessment, this.props.student.id)
              }}
            >
            Copy sheet
            </MenuItem>
          )
          ,

          this.props.assessment.members.length > 1 || this.props.assessments.length > 1 && (
            <MenuItem
              key={"moveStudent"}
              onClick={() => {
                this.handleStudentMenuClose()
                this.props.showMoveStudentModal(this.props.student, this.props.assessment)
              }}
            >
            Move student
            </MenuItem>
          )
          ]}
        </Menu>
      </>
    )
  }
}

export default MoveStudentMenu;