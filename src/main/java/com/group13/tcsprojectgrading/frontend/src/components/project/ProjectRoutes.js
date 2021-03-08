import React, {Component} from "react";
import {Route, Switch} from "react-router-dom";
import Project from "./Project";
import Rubric from "../rubric/Rubric";
import GraderManagement from "../assign/GraderManagement";
import Groups from "../groups/Groups";
import Grading from "../grading/Grading";
import Feedback from "../feedback/Feedback";
import RubricNew from "../rubricNew/RubricNew";


class ProjectRoutes extends Component {
  componentDidMount() {
    console.log(this.props.match.path);
  }

  render () {
    return (
      <Switch>
        <Route path={this.props.match.path + "/groups"} component={Groups}/>
        <Route path={this.props.match.path + "/rubric"} component={RubricNew}/>
        <Route path={this.props.match.path + "/graders"} component={GraderManagement}/>
        <Route path={this.props.match.path + "/grading"} component={Grading}/>
        <Route path={this.props.match.path + "/feedback"} component={Feedback}/>
        <Route path={this.props.match.path + ""} component={Project}/>
      </Switch>
    )
  }
}

export default ProjectRoutes;