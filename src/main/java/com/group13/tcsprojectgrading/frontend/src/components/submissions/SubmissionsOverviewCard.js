import React, {Component} from "react";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import {Badge, Card, Breadcrumb, Button, ListGroup, ListGroupItem, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import {push} from "connected-react-router";
import styles from "./submissions.module.css"
import {Link, withRouter} from "react-router-dom";
import {COURSES} from "../../services/endpoints";

class SubmissionsOverviewCard extends Component {

  componentDidMount() {
    console.log(this.props.route)
  }
  render() {
    return (
      <Card>
        <Card.Body>
          <Card.Title>
            <h5>
              {this.props.submission.name}
              {
                (this.props.submission.isGroup)?
                  <Badge className={styles.badge} variant="success">Group</Badge> :
                  <Badge className={styles.badge} variant="danger">Individual</Badge>
              }

              {
                (this.props.submission.progress <= 0)?
                  <Badge className={styles.badge} variant="danger">not started</Badge> :
                  (this.props.submission.progress < 100)?
                    <Badge className={styles.badge} variant="warning">grading</Badge> :
                    <Badge className={styles.badge} variant="success">finished</Badge>
              }

              {
                (this.props.submission.hasOwnProperty("grader") && this.props.submission.grader.id === this.props.user.id)?
                  <Badge className={styles.badge} variant="info">assigned to you</Badge> :
                  <Badge className={styles.badge} variant="dark">not yours</Badge>
              }
            </h5>
          </Card.Title>
          <div>
            <div className={styles.taskInfo}>
              <h6>Progress: {this.props.submission.progress}%</h6>
              <h6>Submitted At: {this.props.submission.submittedAt}</h6>
              <h6>Attempt: {this.props.submission.attempt}</h6>

              <Button variant="primary" className={styles.goTaskButton}>
                <Link className={styles.plainLink} to={{
                  // pathname: URL_PREFIX + "/" + COURSE_INFO + "/" + this.state.course.id
                  // pathname: `${URL_PREFIX}/${COURSES}/${this.state.course.id}`
                  pathname: this.props.route.url + "/" + this.props.submission.id,
                }}>Open
                </Link>
              </Button>
            </div>
          </div>
        </Card.Body>
      </Card>
    );
  }
}

export default SubmissionsOverviewCard