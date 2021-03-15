import React, {Component} from "react";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import {Badge, Card, Breadcrumb, Button, ListGroup, ListGroupItem, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import {push} from "connected-react-router";
import styles from "./tasks.module.css"
import {Link, withRouter} from "react-router-dom";
import {COURSES} from "../../services/endpoints";

class TaskOverviewCard extends Component {

  componentDidMount() {
    console.log(this.props.route)
  }
  render() {
    return (
      <Card>
        <Card.Body>
          <Card.Title>
            <h5>
              {this.props.task.name}
              {
                (this.props.task.isGroup)?
                  <Badge className={styles.badge} variant="success">Group</Badge> :
                  <Badge className={styles.badge} variant="danger">Individual</Badge>
              }

              {
                (this.props.task.progress <= 0)?
                  <Badge className={styles.badge} variant="danger">not started</Badge> :
                  (this.props.task.progress < 100)?
                    <Badge className={styles.badge} variant="warning">grading</Badge> :
                    <Badge className={styles.badge} variant="success">finished</Badge>
              }
            </h5>
          </Card.Title>
          <div>
            <div className={styles.taskInfo}>
              <h6>Progress: {this.props.task.progress}%</h6>
              <h6>Submission Id: {this.props.task.progress}</h6>
              <h6>Submitted At: {this.props.task.submittedAt}</h6>
              <h6>Attempt: {this.props.task.attempt}</h6>

              <Button variant="primary" className={styles.goTaskButton}>
                <Link className={styles.plainLink} to={{
                  // pathname: URL_PREFIX + "/" + COURSE_INFO + "/" + this.state.course.id
                  // pathname: `${URL_PREFIX}/${COURSES}/${this.state.course.id}`
                  pathname: this.props.route.url + "/" + this.props.task.isGroup + "/" + this.props.task.taskId,
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

export default TaskOverviewCard