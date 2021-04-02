import React, {Component} from "react";
import styles from "../submissionDetails.module.css";
import {ListGroup, ListGroupItem} from "react-bootstrap";
import {URL_PREFIX} from "../../../services/config";
import {Link} from "react-router-dom";
import {IoArrowForward} from "react-icons/io5";
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
                pathname: URL_PREFIX + `/courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/submissions/${this.props.params.submissionId}/assessments/${this.props.assessment.id}/grading`,
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
          {/*<p>progress: {this.props.assessment.progress}%</p>*/}
          {/*<p>issues count: {this.props.assessment.issuesCount}</p>*/}
        </div>
        <div>
          Members:
          <ListGroup>
            {this.props.assessment.members.map((member) => {
              return (
                <ListGroupItem key={member.id}>
                  <div>
                    <div className={styles.memberEditingItemHeader}>
                      <h5>{member.name}</h5>
                    </div>
                    <p>sid: {member.sNumber}</p>
                    {/*<p>Is current: {participant.isCurrentAssessment.toString()}</p>*/}
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
