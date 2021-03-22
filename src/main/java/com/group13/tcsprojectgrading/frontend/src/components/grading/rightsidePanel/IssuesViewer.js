import React, { Component } from 'react'
import styles from '../grading.module.css'
import {connect} from "react-redux";
import Card from "react-bootstrap/Card";
import {request} from "../../../services/request";
import Issues from "./Issues";
import IssueCreator from "./IssueCreator";
import classnames from 'classnames';


class IssuesViewer extends Component {
  constructor (props) {
    super(props);

    this.state = {
      issues: [],
      graders: [],
      isCreating: false,
      isLoaded: false,
    }
  }

  componentDidMount() {
    this.fetchIssues();
  }

  fetchIssues = () => {
    Promise.all([
      request(`/api/courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/${this.props.routeParams.assessmentId}/issues`),
      request(`/api/courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/graders`)
    ])
      .then(async([res1, res2]) => {
        const issues = await res1.json();
        const graders = await res2.json();

        // load submission
        this.setState({
          issues: issues,
          graders: graders,
          isLoaded: true,
        });
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  createIssue = (obj) => {
    request(`/api/courses/${this.props.routeParams.courseId}/projects/${this.props.routeParams.projectId}/submissions/${this.props.routeParams.submissionId}/${this.props.routeParams.assessmentId}/issues`, "POST", obj)
      .then(async (response) => {
        let data = await response.json();

        this.setState({
          issues: data,
          isCreating: false
        })
      })
  }

  updateIsCreating = () => {
    this.setState(prevState => ({
      isCreating: !prevState.isCreating
    }))
  }

  updateIssues = (issues) => {
    this.setState({
      issues: issues
    })
  }

  render () {
    if (!this.state.isLoaded) {
      return (<div>Loading</div>)
    }

    return (
      <div className={styles.issuesContainer}>
        <Card className={styles.panelCard}>
          <Card.Body className={classnames(styles.gradeViewerBody)}>
            {
              (!this.state.isCreating)
                ?
                <Issues updateIssues={this.updateIssues} issues={this.state.issues} routeParams={this.props.routeParams} toggleCreatingState={this.updateIsCreating}/>
                :
                <IssueCreator graders={this.state.graders} issues={this.state.issues} routeParams={this.props.routeParams} toggleCreatingState={this.updateIsCreating} createIssue={this.createIssue}/>
            }
          </Card.Body>
        </Card>
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
