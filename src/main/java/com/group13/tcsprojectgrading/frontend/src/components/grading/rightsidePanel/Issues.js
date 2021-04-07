import React, { Component } from 'react'
import {Dropdown, DropdownButton, ButtonGroup} from "react-bootstrap";
import IssueCard from "./IssueCard";
import styles from "../grading.module.css";
import classnames from "classnames";
import globalStyles from "../../helpers/global.module.css";
import {IoAdd} from "react-icons/io5";
import {Can} from "../../permissions/ProjectAbility";
import { subject } from '@casl/ability';


class Issues extends Component {
  constructor(props) {
    super(props);
    this.state = {
      filterChoice: "All"
    }
  }

  onFilterSelectHandler = (eventKey, event) => {
    this.setState({
      filterChoice: eventKey
    })
  }

  filterIssueDropDown = (issue) => {
    let filter = this.state.filterChoice;

    if (filter === "All") {
      return true;
    }
    return issue.status === filter;
  }

  render() {
    return (
      <>
        <div className={classnames(styles.gradingCardTitle, styles.gradingCardTitleWithButton)}>
          <h4>Issues</h4>
          <div className={styles.gradeEditorCardFooter}>
            <Can I="edit" this={subject('Submission', (this.props.submission.grader === null)? {id: -1}:this.props.submission.grader)}>
              <div className={classnames(globalStyles.iconButton, styles.gradingCardTitleButton)}
                // onClick={this.props.toggleCreatingState}>
                onClick={this.props.toggleShow}>
                <IoAdd size={26}/>
              </div>
            </Can>
            <DropdownButton
              as={ButtonGroup}
              key={"primary"}
              id={`dropdown-Primary`}
              variant={"lightGreen"}
              title={"Filter"}
              onSelect={this.onFilterSelectHandler}
            >
              {["All", "divider", "Resolved", "Open"].map((filterS) => {
                if (filterS === "divider") {
                  return <Dropdown.Divider key={filterS}/>
                } else if (filterS === this.state.filterChoice) {
                  return <Dropdown.Item key={filterS} eventKey={filterS} active>{filterS}</Dropdown.Item>
                } else {
                  return <Dropdown.Item key={filterS} eventKey={filterS}>{filterS}</Dropdown.Item>
                }
              })}
            </DropdownButton>
          </div>
        </div>

        <div className={classnames(styles.gradeEditorContentContainer, styles.gradeViewerBodyScroll)}>
          {this.props.issues.length === 0 &&
          <div className={classnames(globalStyles.modalBodyContainerRow, globalStyles.modalBodyContainerRowEmpty)}>
            No issues
          </div>
          }

          {this.props.issues
            .filter((issue) => {
              return this.filterIssueDropDown(issue)
            })
            .map((issue) => {
              return (
                <IssueCard submission={this.props.submission} key={issue.id} issue={issue} routeParams={this.props.routeParams} updateIssue={this.props.updateIssue}/>
              )
            })
          }
        </div>

      </>
    );
  }
}

export default Issues