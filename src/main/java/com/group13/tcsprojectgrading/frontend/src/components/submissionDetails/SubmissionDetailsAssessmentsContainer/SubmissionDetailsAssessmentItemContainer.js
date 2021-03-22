import React, {Component} from "react";
import styles from "../submissionDetails.module.css";
import {request} from "../../../services/request";
import {BASE} from "../../../services/endpoints";
import {Card, Breadcrumb, Button, ListGroup, ListGroupItem, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import store from "../../../redux/store";
import {URL_PREFIX} from "../../../services/config";
import {push} from "connected-react-router";
import {Link} from "react-router-dom";

class SubmissionDetailsAssessmentItemContainer extends Component {
  constructor(props) {
    super(props);

  }

  render() {
    return (
      <div className={styles.memberAssessmentItem}>
        <div className={styles.assessmentCardHalf}>
          <div>
            <h6>id: {this.props.assessment.id}</h6>
            <h6>issues count: {this.props.assessment.issuesCount}</h6>
          </div>
          <div>
            <Button variant="primary" className={styles.gradingButton}>
              <Link
                className={styles.plainLink}
                to={
                  {
                    pathname: URL_PREFIX + `/courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/submissions/${this.props.params.submissionId}/${this.props.assessment.id}/grading`,
                    data: {
                      "submission": this.props.submission,
                    }
                  }}>
                Grade
              </Link>
            </Button>
          </div>
        </div>
        <ListGroup>
          {this.props.assessment.participants.map((participant) => {
            return (
              <ListGroupItem key={"a-"+participant.id}>
                <div className={styles.memberItem}>
                  <h6>name: {participant.name}</h6>
                </div>
              </ListGroupItem>)
          })}
        </ListGroup>
      </div>
    );
  }

}

export default SubmissionDetailsAssessmentItemContainer
