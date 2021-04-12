import React, { Component } from 'react'
import styles from '../grading.module.css'

import {isCriterion as isCriterionChecker} from "../../rubric/helpers";
import {findById} from "../../../redux/rubric/functions";
import {findCriterion} from "../../../redux/grading/functions";
import {setActive} from "../../../redux/grading/actions";
import {request} from "../../../services/request";

import classnames from 'classnames';
import {findUserById} from "../../../redux/user/functions";
import {ability, Can} from "../../permissions/ProjectAbility";
import { subject } from '@casl/ability';
import globalStyles from "../../helpers/global.module.css";
import CardContent from "@material-ui/core/CardContent";
import Card from "@material-ui/core/Card";
import CheckCircleIcon from '@material-ui/icons/CheckCircle';
import RadioButtonUncheckedIcon from '@material-ui/icons/RadioButtonUnchecked';
import {connect} from "react-redux";
import withTheme from "@material-ui/core/styles/withTheme";
import ListIcon from "@material-ui/icons/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import ListItemText from "@material-ui/core/ListItemText";
import Typography from "@material-ui/core/Typography";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import {IconButton} from "@material-ui/core";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import List from "@material-ui/core/List";


class GradeViewer extends Component {
  constructor (props) {
    super(props);

    this.state = {
      isLoaded: false
    }
  }

  makeActive = (id) => {
    if (!ability.can('edit', subject('Submission', (this.props.submission.grader === null)? {id: -1}:this.props.submission.grader))) return

    request(`/api/courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/submissions/${this.props.match.params.submissionId}/assessments/${this.props.match.params.assessmentId}/grades/${id}/activate`, "POST")
      .then(() => {
        this.props.setActive(this.props.selectedElement, id)
      })
      .catch(error => {
        console.error(error.message)
      });
  }

  render () {
    let isCriterion = this.props.element.hasOwnProperty("content") && isCriterionChecker(this.props.element.content.type);
    let isGraded = this.props.grades;

    return (
      <div className={styles.gradeViewerContainer}>

        <Card className={classnames(styles.panelCard, globalStyles.cardShadow)}>
          <CardContent className={styles.gradeViewerBody}>

            <div className={styles.gradingCardTitle}>
              <h4>Grading history</h4>
            </div>

            {isCriterion ?
              (isGraded ?
                <div className={styles.gradeViewerBodyScroll}>

                  <List>
                    {this.props.grades.map((grade, index) => {
                      return (
                        <ListItem key={grade.id} alignItems="flex-start">
                          <ListItemAvatar>
                            <Avatar
                              alt={grade.grader.name}
                              src={grade.grader.avatar.includes("avatar-50") ? "" : grade.grader.avatar}
                            />
                          </ListItemAvatar>

                          <ListItemText
                            primary={
                              <>
                              Grade <span style={{color: this.props.theme.palette.primary.main}}>{grade.grade}</span>
                                {(grade.description != null ? ` with a note '${grade.description}'` : "")}
                              </>
                            }
                            secondary={
                              <>
                                <Typography
                                  component="span"
                                  style={{display: "block"}}
                                  variant="body2"
                                  color="textPrimary"
                                >
                                  Graded by {grade.grader.name} on {new Date(grade.gradedAt).toISOString().replace('T', ' ').substr(0, 19)}
                                </Typography>
                              </>
                            }
                          />

                          <ListItemSecondaryAction>
                            <IconButton edge="end" aria-label="delete" onClick={() => this.makeActive(grade.id)}>
                              {grade.active ?
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
                </div>
                :
                <div className={styles.gradeViewerNotGraded}>
                  <h6>Not graded yet.</h6>
                </div>
              )
              :
              <div className={styles.gradeViewerNotGraded}>
                <ListIcon fontSize="large" />
                <h6>Choose a criterion</h6>
              </div>
            }
          </CardContent>
        </Card>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    selectedElement: state.rubric.selectedElement,
    rubric: state.rubric.rubric,
    users: state.users.users,
    self: state.users.self,

    element: findById(state.rubric.rubric, state.rubric.selectedElement),
    grades: findCriterion(state.grading.assessment.grades, state.rubric.selectedElement)
  };
};

const actionCreators = {
  setActive
}

export default connect(mapStateToProps, actionCreators)(withTheme(GradeViewer))
