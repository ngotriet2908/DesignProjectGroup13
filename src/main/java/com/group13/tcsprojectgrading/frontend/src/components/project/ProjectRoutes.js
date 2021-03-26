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
import Participants from "../participants/Participants";
import ParticipantDetails from "../participantDetails/ParticipantDetails";


class ProjectRoutes extends Component {
  render () {
    return (
      <Switch>
        {/*<Route exact path={this.props.match.path + "/groups"} component={Groups}/>*/}
        <Route exact path={this.props.match.path + "/participants/:participantId"} component={ParticipantDetails}/>
        <Route exact path={this.props.match.path + "/participants"} component={Participants}/>
        <Route exact path={this.props.match.path + "/rubric"} component={Rubric}/>
        <Route exact path={this.props.match.path + "/graders"} component={GraderManagement}/>
        {/*<Route path={this.props.match.path + "/grading"} component={Grading}/>*/}
        {/*<Route path={this.props.match.path + "/tasks/:taskId/grading"} component={Grading}/>*/}
        <Route exact path={this.props.match.path + "/submissions/:submissionId/:assessmentId/grading"} component={Grading}/>
        <Route exact path={this.props.match.path + "/submissions/:submissionId"} component={SubmissionDetails}/>
        <Route exact path={this.props.match.path + "/submissions"} component={Submissions}/>
        <Route exact path={this.props.match.path + "/feedback"} component={Feedback}/>
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