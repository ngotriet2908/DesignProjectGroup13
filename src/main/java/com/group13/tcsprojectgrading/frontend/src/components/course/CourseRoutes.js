import React, {Component} from "react";
import {Redirect, Route, Switch} from "react-router-dom";
import Course from "./Course";
import ProjectRoutes from "../project/ProjectRoutes";
import {URL_PREFIX} from "../../services/config";
import NotFound from "../error/NotFound";


class CourseRoutes extends Component {
  componentDidMount() {
    console.log(this.props.match.path);
  }

  render () {
    return (
      <Switch>
        <Route exact path={this.props.match.path + ""} component={Course}/>
        <Route path={this.props.match.path + "/projects/:projectId"} component={ProjectRoutes}/>
        <Route>
          <Redirect to={URL_PREFIX + "/404/"}/>
          <NotFound/>
        </Route>
      </Switch>
    )
  }
}

export default CourseRoutes;