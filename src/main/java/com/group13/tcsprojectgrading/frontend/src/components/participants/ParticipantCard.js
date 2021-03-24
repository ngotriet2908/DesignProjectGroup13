import React, {Component} from "react";
import {Breadcrumb, Button, ListGroup, ListGroupItem, Card, Badge} from "react-bootstrap";
import styles from "./participants.module.css";
import {Link} from "react-router-dom";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {deleteRubric, saveRubric} from "../../redux/rubric/actions";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";

class ParticipantCard extends Component {

  render() {
    return (
      <Card>
        <Card.Body>
          <Card.Title>
            <h5>
              {this.props.participant.name}
            </h5>
          </Card.Title>
          <div>
            <div className={styles.groupInfo}>
              <h6>Status: {this.props.participant.status}</h6>
            </div>
              <div>
                <h6>Submissions: {this.props.participant.submissions.length}</h6>
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

const mapStateToProps = state => {
  return {
  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(ParticipantCard)