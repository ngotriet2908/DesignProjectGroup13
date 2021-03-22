import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import {request} from "../../../services/request";
import IssuesView from "./IssuesView";
import IssueCreater from "./IssueCreater";

class IssuesPanel extends Component {
  constructor (props) {
    super(props);
    this.state = {
      isCreating: false,
    }
  }

  componentDidMount() {
    console.log(this.props.issues)
  }

  createIssue = (obj) => {
    request(`/api/courses/${this.props.params.courseId}/projects/${this.props.params.projectId}/submissions/${this.props.params.submissionId}/${this.props.params.assessmentId}/issues`, "POST", obj)
      .then((response) => {
        return response.json()
      })
      .then((data) => {
        this.props.updateIssues(data)
        this.setState({
          isCreating: false
        })
      })
  }

  updateIsCreating = (value) => {
    this.setState({
      isCreating: value
    })
  }

  render () {
    return (
      <div className={styles.issuesPanelContainer}>
        {
          (!this.state.isCreating)?
            <IssuesView updateIssues={this.props.updateIssues} issues={this.props.issues} params={this.props.params} createIssue={() => this.updateIsCreating(true)}/> :
            <IssueCreater graders={this.props.graders} issues={this.props.issues} params={this.props.params} cancelCreateIssue={() => this.updateIsCreating(false)} createIssue={this.createIssue}/>
        }
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric,
  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(IssuesPanel)
