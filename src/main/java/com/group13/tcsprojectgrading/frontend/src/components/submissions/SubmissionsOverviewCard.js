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
            {
              (this.props.submission.isGroup)?
                <Badge className={styles.badge} variant="green">Group</Badge> :
                <Badge className={styles.badge} variant="tomato">Individual</Badge>
            }

            {
              (this.props.submission.progress <= 0)?
                <Badge className={styles.badge} variant="red">Not started</Badge> :
                (this.props.submission.progress < 100)?
                  <Badge className={styles.badge} variant="orange">In progress</Badge> :
                  <Badge className={styles.badge} variant="green">Graded</Badge>
            }

            {
              (this.props.submission.hasOwnProperty("grader") && this.props.submission.grader.id === this.props.user.id)?
                <Badge className={styles.badge} variant="green">Assigned to you</Badge> :
                <Badge className={styles.badge} variant="red">Not assigned to you</Badge>
            }

            {
              (this.props.submission.issuesCount > 0)?
                <Badge className={styles.badge} variant="tomato">Has issues</Badge> : null
            }

            {
              <div style={{marginLeft: "1rem"}}>
                {
                  (this.props.submission.flags.map((flag) => {
                    return (<OverlayTrigger
                      placement="bottom"
                      delay={{ show: 250, hide: 400 }}
                      overlay={(props) => (
                        <Tooltip id={flag.id} {...props}>
                          flag description: {flag.description}
                        </Tooltip>)
                      }>
                      <Badge style={{marginRight:"0.5rem"}} variant={flag.variant}>{flag.name}</Badge>
                    </OverlayTrigger>)
                  }))
                }
              </div>
            }

            <div className={styles.submissionCardHeaderButtonContainer}>
              <div className={classnames(globalStyles.iconButton)} onClick={() => store.dispatch(push(this.props.route.url + "/" + this.props.submission.id))}>
                <IoArrowForward size={26}/>
              </div>
            </div>

          </div>
          <div className={styles.submissionCardBody}>
            <div>Submitted on {(new Date(this.props.submission.submittedAt)).toDateString()}</div>
            <div>Progress: {this.props.submission.progress}%</div>
            <ListGroup>
              Participants:
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