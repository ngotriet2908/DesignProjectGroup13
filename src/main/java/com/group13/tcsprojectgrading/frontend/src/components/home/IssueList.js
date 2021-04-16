import React, { Component } from 'react'
import gradingStyles from '../grading/grading.module.css'
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
import withTheme from "@material-ui/core/styles/withTheme";
import TableFilter from "../helpers/TableFilter";
import IssueCard from "./IssueCard";


class IssueList extends Component {
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
        <div className={classnames(gradingStyles.gradingCardTitle, gradingStyles.gradingCardTitleWithButton, gradingStyles.issuesTitle)}>
          <h4>Issues</h4>
          <div className={gradingStyles.gradeEditorCardFooter}>
            <TableFilter
              options={this.filterOptions}
              selected={this.state.filterChoice}
              setSelected={(index) => this.setState({filterChoice: index})}
              size={"medium"}
            />
          </div>
        </div>

        <div className={classnames(gradingStyles.gradeViewerBodyScroll)}>
          {/*{this.props.issues.length === 0 &&*/}

          {/*}*/}

          {this.props.issues
            .filter((issue) => {
              return this.filterIssueDropDown(issue)
            })
            .map((issue) => {
              return (
                <IssueCard
                  key={issue.id}
                  issue={issue}
                />
              )
            })
          }
        </div>

      </>
    );
  }
}

export default withTheme(IssueList);
