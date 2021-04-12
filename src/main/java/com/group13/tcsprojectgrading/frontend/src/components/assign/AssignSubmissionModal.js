import React, {Component} from "react";
import {request} from "../../services/request";
import {BASE, PROJECT, USER_COURSES} from "../../services/endpoints";
import {connect} from "react-redux";
import classnames from 'classnames';
import globalStyles from "../helpers/global.module.css";
import {Can} from "../permissions/ProjectAbility";
import CustomModal from "../helpers/CustomModal";
import AssignmentReturnIcon from "@material-ui/icons/AssignmentReturn";
import withTheme from "@material-ui/core/styles/withTheme";
import Button from "@material-ui/core/Button";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import {IconButton} from "@material-ui/core";
import CheckCircleIcon from "@material-ui/icons/CheckCircle";
import RadioButtonUncheckedIcon from "@material-ui/icons/RadioButtonUnchecked";
import List from "@material-ui/core/List";
import Pluralize from "pluralize";


class AssignSubmissionModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isLoaded: false,
      selected: null,
    }
  }

  onShow = () => {
    // set the selected grader
    this.setState({
      selected: this.props.currentGrader,
      isLoaded: true
    })
  }

  onClose = () => {
    this.setState({
      isLoaded: false,
      selected: {},
    })

    this.props.toggleShow();
  }

  onAccept = () => {
    let body = this.state.selected.id;

    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.submission.id}/assign`,
      "POST",
      body
    ).then(async () => {
      // let data = await response.json();

      this.setState({
        isLoaded: false,
        selected: null,
      })

      this.props.toggleShow();
      this.props.reloadPage();
    })
  }

  handleGraderClick = (grader) => {
    this.setState(prevState => ({
      selected: grader,
    }))
  }

  disassociateSubmission = () => {
    request(`${BASE}courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.submission.id}/dissociate`,
      "POST",
    ).then(async () => {
      this.setState({
        isLoaded: false,
        selected: null,
      })

      this.props.toggleShow();
      this.props.reloadPage();
    })
  }

  body = () => {
    return (
      <>
        {this.props.graders.length === 0 &&
            <div className={classnames(globalStyles.modalBodyContainerRow, globalStyles.modalBodyContainerRowEmpty)}>
              No graders available in this project
            </div>
        }

        <List>
          {this.props.graders.map(grader => {
            const eq = this.state.selected != null && this.state.selected.id === grader.id;

            return(
              <ListItem key={grader.id}>
                <ListItemAvatar>
                  <Avatar
                    alt={grader.name}
                    src={grader.avatar.includes("avatar-50") ? "" : grader.avatar}
                  />
                </ListItemAvatar>

                <ListItemText
                  primary={
                    <span>{grader.name}</span>
                  }
                  secondary={
                    <span>{Pluralize( 'submission', grader.submissions.length, true)}</span>
                  }
                />

                <ListItemSecondaryAction>
                  <IconButton edge="end" aria-label="delete" onClick={() => this.handleGraderClick(grader)}>
                    {eq ?
                      <CheckCircleIcon style={{color: this.props.theme.palette.success.main}}/>
                      :
                      <RadioButtonUncheckedIcon/>
                    }
                  </IconButton>
                </ListItemSecondaryAction>
              </ListItem>
            )
          })}
        </List>
      </>
    )
  }

  render() {
    return(
      <CustomModal
        show={this.props.show}
        onClose={this.props.toggleShow}
        onShow={this.onShow}
        onAccept={this.onAccept}
        title={"Assign"}
        description={`Choose a person who will be responsible for grading the submission '${(this.props.submission != null)? this.props.submission.name : null}' or leave the submission unassigned.`}
        body={this.body()}
        isLoaded={this.state.isLoaded}

        additionalAction={
          this.props.currentGrader != null &&

          <Button
            disableElevation
            onClick={this.disassociateSubmission}
            style={{backgroundColor: this.props.theme.palette.error.main, color: "white"}}
            startIcon={<AssignmentReturnIcon/>}
          >
            Return submission
          </Button>
        }
      />
    )
  }
}

export default connect(null, null)(withTheme(AssignSubmissionModal))