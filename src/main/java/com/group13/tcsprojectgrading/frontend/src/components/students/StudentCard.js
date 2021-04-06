import React, {Component} from "react";
import {Breadcrumb, Button, ListGroup, ListGroupItem, Card, Badge} from "react-bootstrap";
import styles from "./students.module.css";
import {Link} from "react-router-dom";
import store from "../../redux/store";
import {push} from "connected-react-router";

import {colorToStyles} from "../submissionDetails/labels/LabelRow";
import {deleteRubric, saveRubric} from "../../redux/rubric/actions";
import {setCurrentLocation} from "../../redux/navigation/actions";
import {connect} from "react-redux";
import globalStyles from "../helpers/global.module.css";
import classnames from "classnames";
import {IoArrowForward} from "react-icons/io5";

class StudentCard extends Component {

  render() {
    return (
      <Card className={styles.participantCard}>
        <Card.Body>
          <div className={styles.participantCardTitle}>
            <h5>
              {this.props.participant.id.user.name}
            </h5>

            <div className={styles.participantCardHeaderButtonContainer}>
              <div className={classnames(globalStyles.iconButton)}
                   onClick={() => store.dispatch(push(this.props.match.url +"/"+ this.props.participant.id.user.id))}>
                <IoArrowForward size={26}/>
              </div>
            </div>
          </div>

          <div className={styles.participantCardLabels}>
          </div>

          <div className={styles.participantCardBody}>
            <h6>email: {this.props.participant.id.user.email}</h6>
            <h6>sNumber: {this.props.participant.id.user.sNumber}</h6>

            <ListGroup>
              <p>Submissions:</p>
              {
                this.props.participant.submissions
                  .map((submission) => {
                    return (
                      <ListGroupItem key={submission.id}>
                        <div className={styles.userInfo}>
                          <h6>name: {submission.name}</h6>
                          <h6>grader: {(submission.grader !== undefined)? submission.grader:"no grader"} </h6>
                          <div className={classnames(globalStyles.iconButton)}>
                            <div className={styles.participantCardHeaderButtonContainer}
                                 onClick={() => store.dispatch(push(this.props.match.url.split("/").slice(0, this.props.match.url.split("/").length - 1).join("/") + "/submissions/"+ submission.id))}>
                              <IoArrowForward size={26}/>
                            </div>
                          </div>
                        </div>
                      </ListGroupItem>
                    )
                  })
              }
            </ListGroup>
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

export default connect(mapStateToProps, actionCreators)(StudentCard)