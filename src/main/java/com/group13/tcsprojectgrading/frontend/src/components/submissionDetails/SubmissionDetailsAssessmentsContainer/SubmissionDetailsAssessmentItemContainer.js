import React, {Component} from "react";
import styles from "../submissionDetails.module.css";
import {Button, ListGroup, ListGroupItem, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import store from "../../../redux/store";
import {URL_PREFIX} from "../../../services/config";
import {push} from "connected-react-router";
import {Link} from "react-router-dom";
import {IoArrowForward, IoSwapHorizontal} from "react-icons/io5";
import classnames from 'classnames';
import globalStyles from "../../helpers/global.module.css";


class SubmissionDetailsAssessmentItemContainer extends Component {
  constructor(props) {
    super(props);

  }

  render() {
    return (
      <div className={styles.memberAssessmentItem}>
        <div className={styles.memberAssessmentHeader}>
          <h4>
            Assessment
          </h4>

          <Link
            to={
              {
                pathname: URL_PREFIX + `/courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/submissions/${this.props.params.submissionId}/${this.props.assessment.id}/grading`,
                data: {
                  "submission": this.props.submission,
                }
              }}>
            <div className={classnames(globalStyles.iconButton, styles.primaryButton)}
            >
              <IoArrowForward size={26}/>
            </div>
          </Link>
        </div>
        <div>
          <p>id: {this.props.assessment.id}</p>
          <p>issues count: {this.props.assessment.issuesCount}</p>
        </div>
        <div>
          Members:
          <ListGroup>
            {this.props.assessment.participants.map((participant) => {
              return (
                <ListGroupItem key={"a-"+participant.id}>
                  <div>
                    <div className={styles.memberEditingItemHeader}>
                      <h5>{participant.name}</h5>
                    </div>
                    <p>sid: {participant.sid}</p>
                  </div>
                </ListGroupItem>)
            })}
          </ListGroup>
        </div>
      </div>
    );
  }

}

export default SubmissionDetailsAssessmentItemContainer;
