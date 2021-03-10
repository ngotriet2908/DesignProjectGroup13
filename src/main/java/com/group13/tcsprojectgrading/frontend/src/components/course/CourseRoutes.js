import React, {Component} from "react";
import {Route, Switch} from "react-router-dom";
import Course from "./Course";
import ProjectRoutes from "../project/ProjectRoutes";


class CourseRoutes extends Component {
  componentDidMount() {
    console.log(this.props.match.path);
  }

  render () {
    return (
      // <div>
      <Switch>
        <Route exact path={this.props.match.path + ""} component={Course}/>
        <Route path={this.props.match.path + "/projects/:projectId"} component={ProjectRoutes}/>
      </Switch>
      // </div>
    )
  }
}

export default CourseRoutes;