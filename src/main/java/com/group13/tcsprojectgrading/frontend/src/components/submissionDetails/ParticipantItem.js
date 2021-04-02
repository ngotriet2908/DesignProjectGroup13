import React, {Component} from "react";
import styles from "./submissionDetails.module.css";
import {ListGroupItem} from "react-bootstrap";


class ParticipantItem extends Component {
  render() {
    return (
      <ListGroupItem key={this.props.member.sNumber}>
        <div className={styles.memberItem}>
          <div className={styles.memberAssessmentHeader}>
            <h5>
              {this.props.member.name}
            </h5>
          </div>
          <h6>sid: {this.props.member.sNumber}</h6>
        </div>
      </ListGroupItem>
    );
  }
}

export default ParticipantItem