import React, { Component } from 'react'
import styles from '../grading.module.css'
import {request} from "../../../services/request";
import classnames from "classnames";
import globalStyles from "../../helpers/global.module.css";
import {Can} from "../../permissions/ProjectAbility";
import { subject } from '@casl/ability';
import Chip from "@material-ui/core/Chip";
import withTheme from "@material-ui/core/styles/withTheme";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import KeyboardArrowDownIcon from '@material-ui/icons/KeyboardArrowDown';
import CheckCircleOutlineIcon from '@material-ui/icons/CheckCircleOutline';
import IconButton from "@material-ui/core/IconButton";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import Grid from "@material-ui/core/Grid";
import DescriptionIcon from '@material-ui/icons/Description';
import Tooltip from "@material-ui/core/Tooltip";
import LinkIcon from '@material-ui/icons/Link';
import ContactMailIcon from '@material-ui/icons/ContactMail';


class IssueCard extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isExpanded: false,
      isSolving: false,

      solution: ""
    }
  }

  expandHandler = () => {
    this.setState((prevState) => {
      return {
        isExpanded: !prevState.isExpanded
      }
    })
  }

  submitSolution = () => {
    let solution = this.state.solution;

    if (solution === "") {
      return;
    }

    request(`/api/courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/assessments/${this.props.routeParams.assessmentId}/issues/${this.props.issue.id}/resolve`,
      "POST",
      solution)
      .then(async (response) => {
        let data = await response.json();

        this.setState({
          isSolving: false,
          isExpanded: false,
        })

        this.props.updateIssue(data);
      })
  }

  cancelResolve = () => {
    this.setState({
      isSolving: false,
      isExpanded: false,

      solution: ""
    })
  }

  startResolve = () => {
    this.setState({
      isSolving: true,
      isExpanded: true,
    })
  }

  render() {
    return (
      <Card className={classnames(styles.issueCard, this.state.isExpanded && styles.issuesCardExpanded, globalStyles.cardShadow)}>
        <CardContent>
          <div className={styles.issueCardTitle}>
            <h5>
              {this.props.issue.subject}
            </h5>
            <div className={styles.gradeEditorCardFooter}>
              <IconButton onClick={this.expandHandler} className={styles.issuesCardExpandButton} size={"small"}>
                <KeyboardArrowDownIcon/>
              </IconButton>

              {!(this.props.issue.status === "Resolved") &&
              <Can I="edit" this={subject('Submission', (this.props.submission.grader === null)? {id: -1}:this.props.submission.grader)}>
                <IconButton onClick={this.startResolve} size={"small"}>
                  <CheckCircleOutlineIcon style={{color: this.props.theme.palette.success.main}}/>
                </IconButton>
              </Can>
              }
            </div>
          </div>

          <div className={styles.issueCardBadges}>
            {(this.props.issue.status === "Resolved")?
              <Chip
                label={"Resolved"}
                size="small"
                style={{backgroundColor: this.props.theme.palette.labels["green"]}}
                className={classnames(globalStyles.label)}
              />
              :
              <Chip
                style={{backgroundColor: this.props.theme.palette.labels["red"]}}
                label={"Open"}
                size="small"
                className={classnames(globalStyles.label)}
              />
            }
          </div>

          <Grid container>

            <Grid item sm={12}>
              Opened by {this.props.issue.creator.name}
            </Grid>

            {(this.state.isExpanded) &&
              <>
                <Grid item sm={6}>
                  <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
                    <Tooltip title="Description">
                      <DescriptionIcon style={{color: this.props.theme.palette.primary.main}}/>
                    </Tooltip>
                    <span>{this.props.issue.description}</span>
                  </div>
                </Grid>

                {(this.props.issue.hasOwnProperty("reference")) &&
                <Grid item sm={6}>
                  <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
                    <Tooltip title="Refers to">
                      <LinkIcon style={{color: this.props.theme.palette.primary.main}}/>
                    </Tooltip>
                    <span>{this.props.issue.reference.subject}</span>
                  </div>
                </Grid>
                }

                {(this.props.issue.hasOwnProperty("addressee")) &&
                  <Grid item sm={6}>
                    <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
                      <Tooltip title="Addressee">
                        <ContactMailIcon style={{color: this.props.theme.palette.primary.main}}/>
                      </Tooltip>
                      <span>{this.props.issue.addressee.name}</span>
                    </div>
                  </Grid>
                }
              </>
            }

          </Grid>

          {this.state.isExpanded && this.props.issue.hasOwnProperty("solution") &&

            <div className={styles.issueCardSolution}>
              <h5>Solution</h5>
              <div>
                {this.props.issue.solution}
              </div>
            </div>

          }

          {this.props.issue.status !== "Resolved" && this.state.isSolving &&
            <div className={styles.issueCardSolution}>
              <h5>Solution</h5>

              <TextField
                label="Solution"
                placeholder="Enter response to the issue here"
                multiline
                variant="outlined"
                className={styles.gradeEditorCardItem}
                fullWidth
                rows={2}
                value={this.state.solution}
                onChange={(event) => this.setState({solution: event.target.value})}
              />

              <div className={styles.gradeEditorCardFooter}>
                <Button
                  className={styles.gradeEditorCardButton}
                  onClick={this.cancelResolve}
                  disableElevation
                >
                              Cancel
                </Button>
                <Button
                  className={styles.gradeEditorCardButton}
                  variant="contained"
                  color="primary"
                  onClick={this.submitSolution}
                  disableElevation
                >
                              Save
                </Button>
              </div>
              
            </div>
          }
        </CardContent>
      </Card>
    );
  }
}

export default withTheme(IssueCard);