import React, {Component} from "react";
import {push} from "connected-react-router";
import styles from "./submissions.module.css"
import store from "../../redux/store";
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
import {colorToStyles} from "../submissionDetails/labels/LabelRow";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Grid from "@material-ui/core/Grid";
import EmailIcon from "@material-ui/icons/Email";
import ScheduleIcon from '@material-ui/icons/Schedule';
import withTheme from "@material-ui/core/styles/withTheme";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import ListItemText from "@material-ui/core/ListItemText";
import Tooltip from "@material-ui/core/Tooltip";
import PeopleOutlineIcon from '@material-ui/icons/PeopleOutline';
import Chip from "@material-ui/core/Chip";
import Link from "@material-ui/core/Link";


class SubmissionsOverviewCard extends Component {
  render() {
    return (
      <Card className={classnames(globalStyles.cardShadow, styles.submissionCard)}>
        <CardContent>
          <div className={styles.submissionCardTitle}>
            <Link href="#" color="primary" onClick={(event) => {
              event.preventDefault();
              store.dispatch(push("/app/courses/" +
                this.props.routeParams.courseId +
                "/projects/" +
                this.props.routeParams.projectId +
                "/submissions/" +
                this.props.submission.id));
            }}>
              <h4>
                {this.props.submission.name}
              </h4>
            </Link>

          </div>

          <div className={styles.submissionCardLabels}>
            {
              (this.props.submission.grader != null && this.props.submission.grader.id === this.props.user.id)
                ?
                <Chip
                  label={"Assigned to you"}
                  style={{backgroundColor: this.props.theme.palette.labels["blue"]}}
                  className={classnames(globalStyles.label)}
                />
                :
                <Chip
                  label={"Not assigned to you"}
                />
            }

            {this.props.submission.labels.map((label) => {
              return (
                <Chip
                  key={label.id}
                  className={classnames(globalStyles.label)}
                  label={label.name}
                  style={{
                    backgroundColor: this.props.theme.palette.labels[label.color]
                  }}
                />
              )
            })}

          </div>

          <div className={styles.submissionCardBody}>
            <Grid
              container
              className={classnames(globalStyles.cardBodyContent, styles.cardBodyContent)}
            >
              <Grid item sm={6}>
                <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
                  <Tooltip title="Submitted at">
                    <ScheduleIcon style={{color: this.props.theme.palette.primary.main}}/>
                  </Tooltip>
                  <span>{(new Date(this.props.submission.submittedAt)).toDateString()} at {(new Date(this.props.submission.submittedAt)).toLocaleTimeString('it-IT')}</span>
                </div>
              </Grid>

              <Grid item sm={6}>
                <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
                  <Tooltip title="Group or individual submission">
                    <PeopleOutlineIcon style={{color: this.props.theme.palette.primary.main}}/>
                  </Tooltip>
                  <span>{this.props.submission.groupId != null ? "Group": "Individual"}</span>
                </div>
              </Grid>
            </Grid>

            <h5>Students</h5>
            <List
              className={globalStyles.horizontalList}
            >
              {this.props.submission.members.map((student) => {
                return (
                  <ListItem key={student.id}>
                    <ListItemAvatar>
                      <Avatar
                        alt={student.name}
                        src={student.avatar.includes("avatar-50") ? "" : student.id.user.avatar}
                      />
                    </ListItemAvatar>
                    <ListItemText
                      primary={student.name}
                      secondary={`s${student.sNumber}`}/>
                  </ListItem>
                )
              })}
            </List>
          </div>
        </CardContent>
      </Card>
    );
  }
}

export default withTheme(SubmissionsOverviewCard)