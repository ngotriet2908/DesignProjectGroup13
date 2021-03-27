import React, {Component} from "react";
import styles from "./submissionDetails.module.css";
import {Card, Badge,OverlayTrigger, Tooltip, Button, ListGroup, ListGroupItem, Spinner} from "react-bootstrap";
import globalStyles from "../helpers/global.module.css";


class ParticipantItem extends Component {
  render() {
    return (
      <ListGroupItem key={this.props.member.sid}>
        <div className={styles.memberItem}>
          <div className={styles.memberAssessmentHeader}>
            <h5>
              {this.props.member.name}
            </h5>
          </div>
          <h6>sid: {this.props.member.sid}</h6>
          <h6>email: {this.props.member.email}</h6>
        </div>
      </ListGroupItem>
    );
  }
}

export default ParticipantItem