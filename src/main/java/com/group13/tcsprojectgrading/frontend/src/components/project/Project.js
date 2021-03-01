import React, {Component} from "react";
import styles from "../project/project.module.css";
import {request} from "../../services/request";
import {BASE, COURSE_INFO, PROJECT, USER_COURSES} from "../../services/endpoints";
import Button from 'react-bootstrap/Button'
import {URL_PREFIX} from "../../services/config";
import { Route, Switch, Redirect } from 'react-router-dom'
import GraderManagement from "./GraderManagement";

class Project extends Component {

  constructor(props) {
    super(props)

    this.state = {
      project: {},
      course: {}
    }
  }

  componentDidMount() {
    console.log("Project mounted.")
    // console.log(this.props)

    request(`${BASE}${USER_COURSES}/${COURSE_INFO}/${this.props.match.params.course_id}/${PROJECT}/${this.props.match.params.project_id}`)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          project: data.project,
          course: data.course

        })
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render () {
    return (
      <div className={styles.projectContainer}>
          <div className={styles.headers}>
            <h3>{this.state.course.name} > {this.state.project.name}</h3>
          </div>
        <div className={styles.overviewContainer}>
        <Switch>
          <Route exact path={`${URL_PREFIX}/course/${this.props.match.params.course_id}/project/${this.props.match.params.project_id}`}>
              <div className={styles.overviewContainer}>
                <h2>Administration</h2>
                <Button
                  variant="primary"
                  onClick={() =>
                    this.props.history.push(`${URL_PREFIX}/course/${this.props.match.params.course_id}/project/${this.props.match.params.project_id}/groups`)
                  }>
                  Groups
                </Button> {" "}

                <Button
                  variant="primary"
                  onClick={() =>
                    this.props.history.push(`${URL_PREFIX}/course/${this.props.match.params.course_id}/project/${this.props.match.params.project_id}/graders`)
                  }>
                  Manage graders
                </Button>
              </div>
          </Route>


          {/*Sub-routes*/}
          <Route exact path={`${URL_PREFIX}/course/${this.props.match.params.course_id}/project/${this.props.match.params.project_id}/groups`}>
                  <h2>Project groups</h2>
          </Route>

          <Route exact path={`${URL_PREFIX}/course/${this.props.match.params.course_id}/project/${this.props.match.params.project_id}/graders`}
                 component={GraderManagement}
          />
        </Switch>
        </div>
      </div>
    )
  }
}

export default Project;