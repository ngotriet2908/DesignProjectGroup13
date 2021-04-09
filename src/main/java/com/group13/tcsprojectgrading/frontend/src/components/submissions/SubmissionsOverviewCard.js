import React, {Component} from "react";
import {Badge, Card, ListGroup} from "react-bootstrap";
import {push} from "connected-react-router";
import styles from "./submissions.module.css"
import {IoArrowForward} from "react-icons/io5";
import store from "../../redux/store";
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
import {colorToStyles} from "../submissionDetails/labels/LabelRow";
import ButtonTooltip from "../helpers/ButtonTooltip";


class SubmissionsOverviewCard extends Component {
  render() {
    return (
      <Card className={styles.submissionCard}>
        <Card.Body>
          <div className={styles.submissionCardTitle}>
            <h4>
              {this.props.submission.name}
            </h4>

            <div className={styles.submissionCardHeaderButtonContainer}>
              <ButtonTooltip className={classnames(globalStyles.iconButton)} placement="bottom" content="Go to Submission"
                onClick={() => store.dispatch(push("/app/courses/" + this.props.routeParams.courseId + "/projects/" + this.props.routeParams.projectId + "/submissions/" + this.props.submission.id))}>
                <IoArrowForward size={26}/>
              </ButtonTooltip>
            </div>
          </div>

          <div className={styles.submissionCardLabels}>
            {
              (this.props.submission.groupId != null)?
                <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="green">Group</Badge> :
                <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="tomato">Individual</Badge>
            }

            {/*{*/}
            {/*  (this.props.submission.progress <= 0)?*/}
            {/*    <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="tomato">Not started</Badge> :*/}
            {/*    (this.props.submission.progress < 100)?*/}
            {/*      <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="orange">In progress</Badge> :*/}
            {/*      <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="green">Graded</Badge>*/}
            {/*}*/}

            {
              (this.props.submission.grader != null && this.props.submission.grader.id === this.props.user.id)?
                <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="green">Assigned to you</Badge> :
                <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="tomato">Not assigned to you</Badge>
            }

            {/*{*/}
            {/*  (this.props.submission.issuesCount > 0) &&*/}
            {/*    <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="tomato">Has issues</Badge>*/}
            {/*}*/}

            {(this.props.submission.labels.map((label) => {
              return (
                // <OverlayTrigger
                //   key={flag.id}
                //   placement="bottom"
                //   delay={{ show: 250, hide: 400 }}
                //   overlay={(props) => (
                //     <Tooltip id={flag.id} {...props}>
                //       {flag.description}
                //     </Tooltip>)
                //   }>
                // <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant={label.variant}>{label.name}</Badge>
                <Badge key={label.id} className={classnames(globalStyles.label, globalStyles.labelSmall, colorToStyles[label.color])} variant={label.color}>{label.name}</Badge>
                // </OverlayTrigger>
              )}))}
          </div>

          <div className={styles.submissionCardBody}>
            <p>Submitted on {(new Date(this.props.submission.submittedAt)).toDateString()}</p>
            {/*<p>Progress: {this.props.submission.progress}%</p>*/}
            <ListGroup>
              {/*<p>Members:</p>*/}
              {this.props.submission.members.map((member) => {
                return (
                  <ListGroup.Item key={member.id}>
                    <div className={styles.memberItem}>
                      <h6>name: {member.name}</h6>
                      <h6>sid: {member.sNumber}</h6>
                    </div>
                  </ListGroup.Item>)
              })}
            </ListGroup>
          </div>
        </Card.Body>
      </Card>
    );
  }
}

export default SubmissionsOverviewCard