import React, {Component} from "react";
import styles from "../submissionDetails.module.css";
import {Card, ListGroup, ListGroupItem} from "react-bootstrap";
import SubmissionDetailsAssessmentItemContainer from "./SubmissionDetailsAssessmentItemContainer";
import classnames from 'classnames';
import globalStyles from "../../helpers/global.module.css";
import {IoPencilOutline} from "react-icons/io5";
import Button from "react-bootstrap/Button";
import {Can} from "../../permissions/ProjectAbility";
import { subject } from '@casl/ability';


class SubmissionDetailsAssessmentsContainer extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div className={styles.section}>
        <div className={classnames(styles.sectionTitle, styles.sectionTitleWithButton)}>
          <h3 className={styles.sectionTitleH}>Grading sheets</h3>
          {/*<div className={classnames(globalStyles.iconButton, styles.primaryButton)} onClick={this.props.toggleEditing}>*/}
          {/*  <IoPencilOutline size={26}/>*/}
          {/*</div>*/}
          <Can I="edit" this={subject('Submission', (this.props.submission.grader === null)? {id: -1}:this.props.submission.grader)}>
            <Button variant="lightGreen" onClick={this.props.toggleEditing}><IoPencilOutline size={20}/> Edit</Button>
          </Can>
        </div>

        <div className={styles.sectionContent}>
          {this.props.submission.assessments.map((assessment) => {
            return (
              <Card key={assessment.id}>
                <Card.Body>
                  <SubmissionDetailsAssessmentItemContainer
                    params={this.props.params}
                    submission={this.props.submission}
                    assessment={assessment}/>
                </Card.Body>
              </Card>
            )
          })}
        </div>
      </div>
    );
  }
}

export default SubmissionDetailsAssessmentsContainer