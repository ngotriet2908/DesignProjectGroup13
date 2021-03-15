import React, {Component} from "react";
import styles from "./taskInfo.module.css";
import {request} from "../../services/request";
import {BASE} from "../../services/endpoints";
import {Card, Breadcrumb, Button, ListGroup, ListGroupItem, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import store from "../../redux/store";
import {URL_PREFIX} from "../../services/config";
import {push} from "connected-react-router";
import {Link} from "react-router-dom";

class TaskInfo extends Component {
  constructor(props) {
    super(props);

    // TODO: don't save in state entities that won't be changed
    this.state = {
      course: {},
      project: {},
      task: {},
      submission: {},
      isLoading: true
    }
  }

  componentDidMount() {
    request(`${BASE}courses/${this.props.match.params.courseId}/projects/${this.props.match.params.projectId}/tasks/${this.props.match.params.isGroup}/${this.props.match.params.taskId}`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          task: data.task,
          project: data.project,
          course: data.course,
          submission: data.submission,
          isLoading: false
        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  // openGrading = () => {
  //   store.dispatch(push(URL_PREFIX + "/"))
  // }

  render() {
    return (
      (this.state.isLoading) ?
        <Spinner className={styles.spinner} animation="border" role="status">
          <span className="sr-only">Loading...</span>
        </Spinner>
        :
        <div className={styles.container}>
          <Breadcrumb>
            <Breadcrumb.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumb.Item>
            <Breadcrumb.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id))}>
              {this.state.course.name}
            </Breadcrumb.Item>
            <Breadcrumb.Item
              onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id + "/projects/" + this.state.project.id))}>
              {this.state.project.name}
            </Breadcrumb.Item>
            <Breadcrumb.Item
              onClick={() => store.dispatch(push(`${URL_PREFIX}/courses/${this.state.course.id}/projects/${this.state.project.id}/tasks`))}>
              Tasks
            </Breadcrumb.Item>
            <Breadcrumb.Item active>
              {this.state.task.name}
            </Breadcrumb.Item>
          </Breadcrumb>

          <div className={styles.header}>
            <h2>{this.state.task.name}</h2>
            <Button variant="primary" className={styles.gradingButton}>
              <Link className={styles.plainLink} to={{pathname: URL_PREFIX + `/courses/${this.state.course.id}/projects/${this.state.project.id}/submissions/${this.state.submission.id}/grading`, submission: this.state.submission}}>
                Grade
              </Link>
            </Button>
          </div>

          <div className={styles.bodyContainer}>
            <div className={styles.infoContainer}>
              <Card>
                <Card.Body>
                  <Card.Title>
                    Task Info
                  </Card.Title>
                  <h6>Id: {this.state.task.stringId}</h6>
                  <h6>Status: {this.state.submission.workflow_state}</h6>
                  <h6>Attempts: {this.state.submission.attempt}</h6>
                  <h6>Submitted at: {this.state.submission.submitted_at}</h6>
                  <h6>Progress: {this.state.task.progress}</h6>
                  {(this.state.task.grader == null)? null : <h6>Assigned to: {this.state.task.grader.name}</h6>}
                </Card.Body>

              </Card>
            </div>
            <div className={styles.attachmentContainer}>
              <Card>
                <Card.Body>
                  <Card.Title>
                    Attachments
                  </Card.Title>
                  <ListGroup>
                    {this.state.submission.attachments.map((attachment) => {
                      return (
                        <ListGroupItem>
                          <div>
                            <h6>name: {attachment.display_name}</h6>
                            <h6>type: {attachment["content-type"]}</h6>
                            <h6>size: {attachment.size}</h6>
                          </div>
                        </ListGroupItem>)
                    })}
                  </ListGroup>
                </Card.Body>
              </Card>
            </div>
            <div className={styles.discussionContainer}>
              <Card>
                <Card.Body>
                  <Card.Title>
                    Discussion
                  </Card.Title>
                  Working progress
                </Card.Body>
              </Card>
            </div>
            {(this.state.task.members == null) ? null :
              <div className={styles.memberContainer}>
                <Card>
                  <Card.Body>
                    <Card.Title>
                      Members
                    </Card.Title>
                    <ListGroup>
                      {this.state.task.members.map((member) => {
                        return (
                          <ListGroupItem>
                            <div className={styles.memberItem}>
                              <h6>name: {member.name}</h6>
                              <h6>sid: {member.login_id}</h6>
                              {/*<h6>email: {member.email}</h6>*/}
                            </div>
                          </ListGroupItem>)
                      })}
                    </ListGroup>
                  </Card.Body>
                </Card>
              </div>
            }
          </div>

        </div>
    );
  }
}

export default TaskInfo