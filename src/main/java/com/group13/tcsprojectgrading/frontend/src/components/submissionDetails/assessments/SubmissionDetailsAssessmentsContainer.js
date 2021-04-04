import React, {Component} from "react";
import styles from "../submissionDetails.module.css";
import {Card, ListGroup, ListGroupItem} from "react-bootstrap";
import SubmissionDetailsAssessmentItemContainer from "./SubmissionDetailsAssessmentItemContainer";
import classnames from 'classnames';
import globalStyles from "../../helpers/global.module.css";
import {IoPencilOutline} from "react-icons/io5";
import Button from "react-bootstrap/Button";


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
          <Button variant="lightGreen"><IoPencilOutline size={20} onClick={this.props.toggleEditing}/> Edit</Button>
        </div>

        <div className={styles.sectionContent}>
          {/*<Card>*/}
          {/*  <Card.Body>*/}
          {/*<ListGroup>*/}
          {/*  {this.props.submission.assessments.map((assessment) => {*/}
          {/*    return (*/}
          {/*      <ListGroupItem key={assessment.id}>*/}
          {/*        <SubmissionDetailsAssessmentItemContainer*/}
          {/*          params={this.props.params}*/}
          {/*          submission={this.props.submission}*/}
          {/*          assessment={assessment}/>*/}
          {/*      </ListGroupItem>)*/}
          {/*  })}*/}
          {/*</ListGroup>*/}
          {/*</Card.Body>*/}
          {/*</Card>*/}

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