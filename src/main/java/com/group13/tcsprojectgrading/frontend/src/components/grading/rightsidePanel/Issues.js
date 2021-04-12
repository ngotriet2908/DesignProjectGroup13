import React, { Component } from 'react'
import IssueCard from "./IssueCard";
import styles from "../grading.module.css";
import classnames from "classnames";
import globalStyles from "../../helpers/global.module.css";
import {Can} from "../../permissions/ProjectAbility";
import { subject } from '@casl/ability';
import AddIcon from '@material-ui/icons/Add';
import IconButton from "@material-ui/core/IconButton";
import TableFilter from "../../helpers/TableFilter";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import EmptyCourseCard from "../../home/EmptyCourseCard";


class Issues extends Component {
  constructor(props) {
    super(props);

    this.filterOptions = ["All", "Resolved", "Open"]

    this.state = {
      filterChoice: 0
    }
  }

  onFilterSelectHandler = (eventKey, event) => {
    this.setState({
      filterChoice: eventKey
    })
  }

  filterIssueDropDown = (issue) => {
    let filter = this.filterOptions[this.state.filterChoice];

    if (filter === "All") {
      return true;
    }
    return issue.status === filter;
  }

  render() {
    return (
      <>
        <div className={classnames(styles.gradingCardTitle, styles.gradingCardTitleWithButton, styles.issuesTitle)}>
          <h4>Issues</h4>
          <div className={styles.gradeEditorCardFooter}>
            <Can I="edit" this={subject('Submission', (this.props.submission.grader === null)? {id: -1}:this.props.submission.grader)}>
              <IconButton onClick={this.props.toggleShow}>
                <AddIcon/>
              </IconButton>
            </Can>

            <TableFilter
              options={this.filterOptions}
              selected={this.state.filterChoice}
              setSelected={(index) => this.setState({filterChoice: index})}
              size={"medium"}
            />

          </div>
        </div>

        <div className={classnames(styles.gradeViewerBodyScroll)}>
          {this.props.issues.length === 0 &&
            <EmptyCourseCard
              action={this.props.toggleShow}
              description={"Create issue"}
              className={styles.issueCard}
            />
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