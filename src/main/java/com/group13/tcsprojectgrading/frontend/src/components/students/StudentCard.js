import React, {Component} from "react";
import {Button, ListGroup, ListGroupItem, Card, Badge} from "react-bootstrap";
import styles from "./students.module.css";
import store from "../../redux/store";
import {push} from "connected-react-router";
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
import {IoArrowForward} from "react-icons/io5";

class StudentCard extends Component {
  render() {
    return (
      <Card className={styles.studentCard}>
        <Card.Body>
          <div className={styles.studentCardTitle}>
            <h4>
              {this.props.student.id.user.name}
            </h4>

            <div className={styles.studentCardHeaderButtonContainer}>
              <div className={classnames(globalStyles.iconButton)}
                onClick={() => store.dispatch(push(this.props.match.url +"/"+ this.props.student.id.user.id))}>
                <IoArrowForward size={26}/>
              </div>
            </div>
          </div>

          <div className={styles.studentCardBody}>
            <p>Email: {this.props.student.id.user.email}</p>
            <p>sNumber: {this.props.student.id.user.sNumber}</p>
            <p>Submissions: {this.props.student.submissions.length}</p>

            <ListGroup>
              <p>Submissions:</p>
              {this.props.student.submissions.map((submission) => {
                return (
                  <ListGroup.Item key={submission.id}>
                    <div className={styles.submissionItem}>
                      <div className={styles.submissionItemHeader}>
                        <h5>Submission #{submission.id}</h5>

                        <div className={styles.studentCardHeaderButtonContainer}>
                          <div className={classnames(globalStyles.iconButton)}
                            onClick={() => store.dispatch(push(this.props.match.url.split("/").slice(0, this.props.match.url.split("/").length - 1).join("/") + "/submissions/"+ submission.id))}>
                            <IoArrowForward size={26}/>
                          </div>
                        </div>
                      </div>
                      <p>name: {submission.name}</p>
                      <p>grader: {(submission.grader != null) ? submission.grader.name : "no grader"}</p>
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

export default StudentCard;