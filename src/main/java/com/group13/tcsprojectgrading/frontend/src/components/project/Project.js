import React, {Component} from "react";
import styles from "../project/project.module.css";
import {request} from "../../services/request";
import {BASE } from "../../services/endpoints";
import Button from 'react-bootstrap/Button'
import {URL_PREFIX} from "../../services/config";
import {Link, Route, Switch} from 'react-router-dom'
import GraderManagement from "./GraderManagement";

import {v4 as uuidv4} from "uuid";
import {removeRubric, saveRubric} from "../../redux/rubric/actions";
import {connect} from "react-redux";

class Project extends Component {

  constructor(props) {
    super(props)

    this.state = {
      project: {},
      course: {},
      rubric: null,
    }
  }

  componentDidMount() {
    console.log("Project mounted.")
    // console.log(this.props)

    // request(`${BASE}${USER_COURSES}/${COURSE_INFO}/${this.props.match.params.course_id}/${PROJECT}/${this.props.match.params.project_id}`)
    request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          project: data.project,
          course: data.course,
          // rubric: data.rubric,
        })

        this.props.saveRubric(data.rubric);
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  /*
  Creates a new rubric object and saves it to the store
  */
  onClickCreateRubric = () => {
    let rubric = {
      id: uuidv4(),
      blocks: []
    }

    request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric", "POST", rubric)
      .then(data => {
        console.log(data);
        this.props.saveRubric(rubric);
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  onClickRemoveRubric = () => {
    // this.props.removeRubric();

    // TODO: send DELETE
    request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric", "DELETE")
      .then(data => {
        console.log(data);
        this.props.removeRubric();
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
            <Route exact path={URL_PREFIX + "/courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId}>
              <div className={styles.overviewContainer}>
                <h2>Administration</h2>
                <Button
                  variant="primary"
                  onClick={() =>
                    this.props.history.push(this.props.match.url + "/groups")
                  }>
                  Groups
                </Button> {" "}

                <Button
                  variant="primary"
                  onClick={() =>
                    this.props.history.push(this.props.match.url  + "/graders")
                  }>
                  Manage graders
                </Button>
              </div>

              <div>
                {this.props.rubric != null ?
                  <div>
                    <h2>Rubric</h2>
                    <Button variant="primary"><Link className={styles.plainLink} to={this.props.match.url + "/rubric"}>Open
                      rubric</Link></Button>
                    <Button variant="danger" onClick={this.onClickRemoveRubric}>Remove rubric (turned off)</Button>
                  </div>
                  :
                  <div>
                    <div>No rubric</div>
                    <Button variant="primary" onClick={this.onClickCreateRubric}>Create rubric</Button>
                  </div>
                }
              </div>
            </Route>


            {/*Sub-routes*/}
            <Route exact path={URL_PREFIX + "/courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/groups"}>
              <h2>Project groups</h2>
            </Route>

            <Route exact path={URL_PREFIX + "/courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/graders"}
              component={GraderManagement}
            />
          </Switch>
        </div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubric.rubric
  };
};

const actionCreators = {
  saveRubric,
  removeRubric
}

export default connect(mapStateToProps, actionCreators)(Project)