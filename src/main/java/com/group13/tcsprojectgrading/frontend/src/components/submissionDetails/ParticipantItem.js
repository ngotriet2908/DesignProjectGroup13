import React, {Component} from "react";
import styles from "./submissionDetails.module.css";
import {ListGroupItem} from "react-bootstrap";


class ParticipantItem extends Component {
  render() {
    return (
      <div className={styles.memberItem}>
        <div className={styles.memberAssessmentHeader}>
          <h5>
            {this.props.member.name}
          </h5>
        </div>
        <h6>sid: {this.props.member.sNumber}</h6>
        {/*<h6>email: {this.props.member.email}</h6>*/}
      </div>
    );
  }
}

export default ParticipantItem