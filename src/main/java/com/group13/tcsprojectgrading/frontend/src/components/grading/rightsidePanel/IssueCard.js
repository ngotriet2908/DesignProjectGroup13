import React, { Component } from 'react'
import styles from '../grading.module.css'
import {request} from "../../../services/request";
import {Button, Form, Card, Badge} from "react-bootstrap";
import classnames from "classnames";
import globalStyles from "../../helpers/global.module.css";
import {IoCheckmarkDone, IoChevronDownOutline} from "react-icons/io5";


class IssueCard extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isExpanded: false,
      isSolving: false,
    }
    this.formRef = React.createRef()
  }

  expandHandler = () => {
    this.setState((prevState) => {
      return {
        isExpanded: !prevState.isExpanded
      }
    })
  }

  submitSolution = () => {
    let obj = {
      id: this.props.issue.id,
      solution: this.formRef.current.solutionInput.value
    }

    request(`/api/courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/${this.props.routeParams.assessmentId}/issues/resolve`, "POST", obj)
      .then(async (response) => {
        let data = await response.json();
        this.props.updateIssues(data)
        this.setState({
          isSolving: false
        })
      })
  }

  cancelResolve = () => {
    this.setState({
      isSolving: false,
    })
  }

  startResolve = () => {
    this.setState({
      isSolving: true,
    })
  }

  render() {
    return (
      <Card className={classnames(styles.issueCard, this.state.isExpanded && styles.issuesCardExpanded)}>
        <div className={styles.issueCardTitle}>
          <h5>
            {this.props.issue.subject}
          </h5>
          <div className={styles.gradeEditorCardFooter}>
            <div className={classnames(globalStyles.iconButtonSmall, styles.gradingCardTitleButton, styles.issuesCardExpandButton)}
              onClick={this.expandHandler}>
              <IoChevronDownOutline size={26}/>
            </div>
            {!(this.props.issue.status === "resolved") &&
            <div className={classnames(globalStyles.iconButtonSmall, styles.gradingCardTitleButton)}
              onClick={this.startResolve}>
              <IoCheckmarkDone size={26}/>
            </div>
            }
          </div>
        </div>

        <div className={styles.issueCardBadges}>
          {(this.props.issue.status === "resolved")?
            <Badge className={styles.badge} variant="success">resolved</Badge> :
            <Badge className={styles.badge} variant="danger">unresolved</Badge>
          }
        </div>
          
        {(this.state.isExpanded)?
          (
            <div>
              <div>
                Opened by <b>{this.props.issue.creator.name}</b> about <b>{this.props.issue.targetName}</b>.
              </div>

              <div>Subject: {this.props.issue.subject}</div>
              <div>Description: {this.props.issue.description}</div>

              {(this.props.issue.hasOwnProperty("reference")) &&
                  <div>Refers to <b>{this.props.issue.reference.subject}</b></div>
              }

              {(this.props.issue.hasOwnProperty("addressee")) &&
                  <div>Addressee: {this.props.issue.addressee.name}</div>
              }

              {(this.props.issue.hasOwnProperty("solution")) &&
                  <div>Solution: {this.props.issue.solution}</div>
              }
            </div>
          ):
          (
            <div>
              <div>
                Opened by <b>{this.props.issue.creator.name}</b> about <b>{this.props.issue.targetName}</b>.
              </div>
            </div>
          )
        }

        {(this.state.isSolving) &&
          <div className={styles.issueCardSolution}>
            <h5>Solution</h5>
            <Form ref={this.formRef}>
              <Form.Group controlId="solutionInput" className={styles.gradeEditorCardItem}>
                <Form.Control as="textarea" rows={3} placeholder="Enter your response to the issue"/>
              </Form.Group>

              <div className={styles.gradeEditorCardFooter}>
                <Button className={styles.gradeEditorCardButton} variant="linkLightGray"
                  onClick={this.cancelResolve}>Cancel</Button>
                <Button className={styles.gradeEditorCardButton} variant="lightGreen" onClick={this.submitSolution}>Save</Button>
              </div>
            </Form>
          </div>
        }
      </Card>
    );
  }
}

export default IssueCard