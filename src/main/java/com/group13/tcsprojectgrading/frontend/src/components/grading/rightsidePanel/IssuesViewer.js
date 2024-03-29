import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import {request} from "../../../services/request";
import Issues from "./Issues";
import classnames from 'classnames';
import CreateIssueModal from "./CreateIssueModal";
import globalStyles from "../../helpers/global.module.css";
import CardContent from "@material-ui/core/CardContent";
import Card from "@material-ui/core/Card";


class IssuesViewer extends Component {
  constructor (props) {
    super(props);

    this.state = {
      issues: [],
      graders: [],
      isCreating: false,
      isLoaded: false,

      showModal: false,
    }
  }

  componentDidMount() {
    this.fetchIssues();
  }

  fetchIssues = () => {
    Promise.all([
      request(`/api/courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/assessments/${this.props.routeParams.assessmentId}/issues`),
    ])
      .then(async([res1]) => {
        const issues = await res1.json();

        // load submission
        this.setState({
          issues: issues,
          isLoaded: true,
        });
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  toggleShowModal = () => {
    this.setState(prevState => ({
      showModal: !prevState.showModal
    }))
  }

  appendIssue = (issue) => {
    this.setState(prevState => ({
      issues: [...prevState.issues, issue]
    }))
  }

  updateIssue = (updatedIssue) => {
    this.setState(prevState => ({
      issues: prevState.issues.map(issue => {
        return issue.id === updatedIssue.id ? updatedIssue : issue;
      })
    }))
  }

  render () {
    if (!this.state.isLoaded) {
      return (<div>Loading</div>)
    }

    return (
      <div className={styles.issuesContainer}>
        <div className={classnames(styles.panelCard)}>
          <div className={styles.gradeViewerBody}>
            <Issues
              submission={this.props.submission}
              updateIssue={this.updateIssue}
              issues={this.state.issues}
              routeParams={this.props.routeParams}
              toggleShow={this.toggleShowModal}
            />
          </div>
        </div>

        <CreateIssueModal
          user={this.props.user}
          submission={this.props.submission}
          show={this.state.showModal}
          toggleShow={this.toggleShowModal}
          routeParams={this.props.routeParams}
          issues={this.state.issues}
          appendIssue={this.appendIssue}
        />
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {

  };
};

const actionCreators = {

}

export default connect(mapStateToProps, actionCreators)(IssuesViewer)
