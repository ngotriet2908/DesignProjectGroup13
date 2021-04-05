import React, {Component} from "react";
import {Redirect, Route, Switch} from "react-router-dom";
import Project from "./Project";
import Rubric from "../rubric/Rubric";
import GraderManagement from "../assign/GraderManagement";
import Groups from "../groups/Groups";
import Grading from "../grading/Grading";
import Feedback from "../feedback/Feedback";
import Submissions from "../submissions/Submissions";
import SubmissionDetails from "../submissionDetails/SubmissionDetails";
import NotFound from "../error/NotFound";
import {URL_PREFIX} from "../../services/config";
import Students from "../students/Students";
import Settings from "../settings/Settings";
import StudentDetails from "../studentDetails/StudentDetails";


class ProjectRoutes extends Component {
  render () {
    return (
      <Switch>
        <Route exact path={this.props.match.path + "/students/:studentId"} component={StudentDetails}/>
        <Route exact path={this.props.match.path + "/students"} component={Students}/>
        <Route exact path={this.props.match.path + "/rubric"} component={Rubric}/>
        <Route exact path={this.props.match.path + "/graders"} component={GraderManagement}/>
        <Route exact path={this.props.match.path + "/submissions/:submissionId/assessments/:assessmentId/grading"} component={Grading}/>
        <Route exact path={this.props.match.path + "/submissions/:submissionId"} component={SubmissionDetails}/>
        <Route exact path={this.props.match.path + "/submissions"} component={Submissions}/>
        <Route exact path={this.props.match.path + "/feedback"} component={Feedback}/>
        <Route exact path={this.props.match.path + "/settings"} component={Settings}/>
        <Route exact path={this.props.match.path + ""} component={Project}/>
        <Route>
          <Redirect to={URL_PREFIX + "/404/"}/>
          <NotFound/>
        </Route>
      </Switch>
    )
  }
}

export default ProjectRoutes;