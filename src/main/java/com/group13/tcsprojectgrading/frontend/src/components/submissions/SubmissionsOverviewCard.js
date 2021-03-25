import React, {Component} from "react";
import {Badge, Card, Button, OverlayTrigger, Tooltip, ListGroup} from "react-bootstrap";
import {push} from "connected-react-router";
import styles from "./submissions.module.css"
import {Link} from "react-router-dom";
import {IoArrowForward, IoSyncOutline} from "react-icons/io5";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";

class SubmissionsOverviewCard extends Component {
  render() {
    return (
      <Card className={styles.submissionCard}>
        <Card.Body>
          <div className={styles.submissionCardTitle}>
            <h5>
              {this.props.submission.name}
            </h5>

            <div className={styles.submissionCardHeaderButtonContainer}>
              <div className={classnames(globalStyles.iconButton)} onClick={() => store.dispatch(push(this.props.route.url + "/" + this.props.submission.id))}>
                <IoArrowForward size={26}/>
              </div>
            </div>
          </div>

          <div className={styles.submissionCardLabels}>
            {
              (this.props.submission.isGroup)?
                <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="green">Group</Badge> :
                <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="tomato">Individual</Badge>
            }

            {/* TODO doesn't work*/}
            {/*{*/}
            {/*  (this.props.submission.progress <= 0)?*/}
            {/*    <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="tomato">Not started</Badge> :*/}
            {/*    (this.props.submission.progress < 100)?*/}
            {/*      <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="orange">In progress</Badge> :*/}
            {/*      <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="green">Graded</Badge>*/}
            {/*}*/}

            {
              (this.props.submission.hasOwnProperty("grader") && this.props.submission.grader.id === this.props.user.id)?
                <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="green">Assigned to you</Badge> :
                <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="tomato">Not assigned to you</Badge>
            }

            {
              (this.props.submission.issuesCount > 0) &&
                <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant="tomato">Has issues</Badge>
            }

            {(this.props.submission.flags.map((flag) => {
              return (
                <OverlayTrigger
                  key={flag.id}
                  placement="bottom"
                  delay={{ show: 250, hide: 400 }}
                  overlay={(props) => (
                    <Tooltip id={flag.id} {...props}>
                      {flag.description}
                    </Tooltip>)
                  }>
                  <Badge className={classnames(globalStyles.label, globalStyles.labelSmall)} variant={flag.variant}>{flag.name}</Badge>
                </OverlayTrigger>

              )
            }))
            }
          </div>

          <div className={styles.submissionCardBody}>
            <p>Submitted on {(new Date(this.props.submission.submittedAt)).toDateString()}</p>
            <p>Progress: {this.props.submission.progress}%</p>
            <ListGroup>
              <p>Members:</p>
              {this.props.submission.participants.map((member) => {
                return (
                  <ListGroup.Item key={member.id}>
                    <div className={styles.memberItem}>
                      <h6>name: {member.name}</h6>
                      <h6>sid: {member.sid}</h6>
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