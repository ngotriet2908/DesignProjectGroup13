import React, {Component} from "react";
import {Card, Badge,OverlayTrigger, Tooltip, Button, ListGroup, ListGroupItem, Spinner} from "react-bootstrap";
import styles from "./submissionDetails.module.css";
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
import {IoCopyOutline, IoTrashOutline, IoSwapHorizontal} from "react-icons/io5";

class ParticipantItemEdit extends Component {
  render() {
    return (
      <div className={styles.memberItem}>

        <div className={styles.memberAssessmentHeader}>
          <h5>
            {this.props.member.name}
          </h5>
          <div className={classnames(globalStyles.iconButtonSmall, styles.dangerButton)}
            onClick={this.props.handleDelete}>
            <IoTrashOutline size={26}/>
          </div>
        </div>
        <h6>sid: {this.props.member.sNumber}</h6>
        {/*<h6>email: {this.props.member.email}</h6>*/}
      </div>
    );
  }
}

export default ParticipantItemEdit