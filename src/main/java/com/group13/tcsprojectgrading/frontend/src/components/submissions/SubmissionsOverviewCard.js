import React, {Component} from "react";
import {Badge, Card, Button} from "react-bootstrap";
import {push} from "connected-react-router";
import styles from "./submissions.module.css"
import {Link} from "react-router-dom";
import {IoArrowForward} from "react-icons/io5";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";

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
                (this.props.submission.progress < 1)?
                  <Badge className={styles.badge} variant="orange">In progress</Badge> :
                  <Badge className={styles.badge} variant="green">Graded</Badge>
            }

            {
              (this.props.submission.hasOwnProperty("grader") && this.props.submission.grader.id === this.props.user.id)?
                <Badge className={styles.badge} variant="green">Assigned to you</Badge> :
                <Badge className={styles.badge} variant="red">Not assigned to you</Badge>
            }

            <div className={styles.submissionCardHeaderButtonContainer}>
              <div onClick={() => store.dispatch(push(this.props.route.url + "/" + this.props.submission.id + "/grading"))}>
                <IoArrowForward size={26}/>
              </div>
            </div>

          </div>
          <div className={styles.submissionCardBody}>
            <div>Progress: {Math.round(100 * this.props.submission.progress)}%</div>
            <div>Submitted on {(new Date(this.props.submission.submittedAt)).toDateString()}</div>
            <div>Attempts: {this.props.submission.attempt}</div>

            {/*<Button variant="primary" className={styles.goTaskButton}>*/}
            {/*  <Link className={styles.plainLink} to={{*/}
            {/*    pathname: this.props.route.url + "/" + this.props.submission.id,*/}
            {/*  }}>Open*/}
            {/*  </Link>*/}
            {/*</Button>*/}
          </div>
        </Card.Body>
      </Card>
    );
  }
}

export default SubmissionsOverviewCard