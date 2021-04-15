import React, { Component } from 'react'
import gradingStyles from '../grading/grading.module.css'
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
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
import Link from "@material-ui/core/Link";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {URL_PREFIX} from "../../services/config";
import {COURSES} from "../../services/endpoints";


class IssueCard extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isExpanded: false,
    }
  }

  expandHandler = () => {
    this.setState((prevState) => {
      return {
        isExpanded: !prevState.isExpanded
      }
    })
  }


  render() {
    return (
      <Card className={classnames(gradingStyles.issueCard, this.state.isExpanded && gradingStyles.issuesCardExpanded, globalStyles.cardShadow)}>
        <CardContent>
          <div className={gradingStyles.issueCardTitle}>
            <h5>
              {this.props.issue.subject}
            </h5>
            <div className={gradingStyles.gradeEditorCardFooter}>
              <IconButton onClick={this.expandHandler} className={gradingStyles.issuesCardExpandButton} size={"small"}>
                <KeyboardArrowDownIcon/>
              </IconButton>
            </div>
          </div>

          <div className={gradingStyles.issueCardBadges}>
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
              <span>Opened by {this.props.issue.creator.name} in submission </span>
              <Link href="#" color="primary" onClick={(event) => {
                event.preventDefault();
                store.dispatch(push("/app/courses/" +
                  this.props.issue.course.id +
                  "/projects/" +
                  this.props.issue.project.id +
                  "/submissions/" +
                  this.props.issue.submission.id +
                  "/assessments/" +
                  this.props.issue.assessment +
                  "/grading"
                ));
              }}>
                <span>{this.props.issue.submission.name} </span>
              </Link>
              <span>of project </span>
              <Link href="#" color="primary" onClick={(event) => {
                event.preventDefault();
                store.dispatch(push("/app/courses/" +
                  this.props.issue.course.id +
                  "/projects/" +
                  this.props.issue.project.id
                ));
              }}>
                <span>{this.props.issue.project.name}</span>
              </Link>
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

          <div className={gradingStyles.issueCardSolution}>
            <h5>Solution</h5>
            <div>
              {this.props.issue.solution}
            </div>
          </div>

          }
        </CardContent>
      </Card>
    );
  }
}

export default withTheme(IssueCard);