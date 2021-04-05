import React, {Component} from "react";
import {Button, ListGroup, ListGroupItem, Card} from "react-bootstrap";
import styles from "./students.module.css";
import store from "../../redux/store";
import {push} from "connected-react-router";

class StudentCard extends Component {
  render() {
    return (
      <Card>
        <Card.Body>
          <Card.Title>
            <h5>
              {this.props.participant.id.user.name}
            </h5>
          </Card.Title>
          <div>
            <div className={styles.groupInfo}>
              <h6>email: {this.props.participant.id.user.email}</h6>
              <h6>sNumber: {this.props.participant.id.user.sNumber}</h6>
            </div>
            <div>
              <h6>Submissions: {this.props.participant.submissions.length}</h6>
              <Button onClick={() => store.dispatch(push(this.props.match.url +"/"+ this.props.participant.id.user.id))}>
                  open </Button>
              {/*<h6>Members List</h6>*/}
              <ListGroup>
                {/*Submissions List*/}
                {this.props.participant.submissions
                  .map((submission) => {
                    return (
                      <ListGroupItem key={submission.id}>
                        <div className={styles.userInfo}>
                          <h6>name: {submission.name}</h6>
                          <h6>grader: {(submission.grader !== undefined)? submission.grader:"no grader"}</h6>
                          <Button onClick={() => store.dispatch(push(this.props.match.url.split("/").slice(0, this.props.match.url.split("/").length - 1).join("/") + "/submissions/"+ submission.id))}>
                              open </Button>
                          {/*<h6>email: {member.email}</h6>*/}
                        </div>
                      </ListGroupItem>
                    )
                  })}
              </ListGroup>
            </div>
          </div>
        </Card.Body>
      </Card>
    );
  }
}

export default StudentCard;