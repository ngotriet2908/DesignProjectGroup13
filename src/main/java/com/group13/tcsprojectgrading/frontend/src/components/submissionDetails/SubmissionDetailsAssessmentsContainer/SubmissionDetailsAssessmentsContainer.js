import React, {Component} from "react";
import styles from "../submissionDetails.module.css";
import {Card, ListGroup, ListGroupItem} from "react-bootstrap";
import SubmissionDetailsAssessmentItemContainer from "./SubmissionDetailsAssessmentItemContainer";
import classnames from 'classnames';
import globalStyles from "../../helpers/global.module.css";
import {IoPencilOutline} from "react-icons/io5";


class SubmissionDetailsAssessmentsContainer extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div className={styles.section}>
        <div className={classnames(styles.sectionTitle, styles.sectionTitleWithButton)}>
          <h3 className={styles.sectionTitleH}>Assessments</h3>
          <div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.props.toggleEditing}>
            <IoPencilOutline size={26}/>
          </div>
        </div>

        <div className={styles.sectionContent}>
          <Card>
            <Card.Body>
              <ListGroup>
                {this.props.submission.assessments.map((assessment) => {
                  return (
                    <ListGroupItem key={assessment.id}>
                      <SubmissionDetailsAssessmentItemContainer
                        params={this.props.params}
                        submission={this.props.submission}
                        assessment={assessment}/>
                    </ListGroupItem>)
                })}
              </ListGroup>
            </Card.Body>
          </Card>
        </div>
      </div>
    );
  }
}

export default SubmissionDetailsAssessmentsContainer