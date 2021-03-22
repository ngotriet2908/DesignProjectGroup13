import React, {Component} from "react";
import styles from "../submissionDetails.module.css";
import {request} from "../../../services/request";
import {BASE} from "../../../services/endpoints";
import {Card, Breadcrumb, Button, ListGroup, ListGroupItem, Spinner, ButtonGroup, DropdownButton, Dropdown, FormControl} from "react-bootstrap";
import store from "../../../redux/store";
import {URL_PREFIX} from "../../../services/config";
import {push} from "connected-react-router";
import {Link} from "react-router-dom";
import SubmissionDetailsAssessmentItemContainer from "./SubmissionDetailsAssessmentItemContainer";

class SubmissionDetailsAssessmentsContainer extends Component {
  constructor(props) {
    super(props);

  }

  render() {
    return (
      <div className={styles.memberContainer}>
        <Card>
          <Card.Body>
            <Card.Title>
              <div style={{display:"inline"}}>
                <h5 style={{display:"inline"}}>Assessments</h5>
                <Button onClick={this.props.toggleEditing} style={{display:"inline", marginLeft:"1rem"}}
                        variant="primary"> Edit </Button>
              </div>
            </Card.Title>
            <ListGroup>
              {this.props.submission.assessments.map((assessment) => {
                return (
                  <ListGroupItem key={assessment.id}>
                    <SubmissionDetailsAssessmentItemContainer params={this.props.params} submission={this.props.submission} assessment={assessment}/>
                  </ListGroupItem>)
              })}
            </ListGroup>
          </Card.Body>
        </Card>
      </div>
    );
  }
}

export default SubmissionDetailsAssessmentsContainer