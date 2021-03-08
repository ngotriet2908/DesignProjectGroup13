import React, {Component} from "react";
import styles from "../project/project.module.css";
import {request} from "../../services/request";
import {BASE } from "../../services/endpoints";
import Button from 'react-bootstrap/Button'
import {URL_PREFIX} from "../../services/config";
import {Link, Route, Switch} from 'react-router-dom'

import {v4 as uuidv4} from "uuid";
import {connect} from "react-redux";
import {Breadcrumb} from "react-bootstrap";
import store from "../../redux/store";
import {push} from "connected-react-router";
import Card from "react-bootstrap/Card";
import {deleteRubric, saveRubric} from "../../redux/rubricNew/actions";

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
    request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId)
      .then(response => {
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          project: data.project,
          course: data.course,
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
    console.log("Creating rubric for project " + parseInt(this.props.match.params.projectId));

    let rubric = {
      id: uuidv4(),
      projectId: this.props.match.params.projectId,
      children: []
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
    request(BASE + "courses/" + this.props.match.params.courseId + "/projects/" + this.props.match.params.projectId + "/rubric", "DELETE")
      .then(data => {
        console.log(data);
        this.props.deleteRubric();
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  render () {
    return (
      <div className={styles.projectContainer}>
        <Breadcrumb>
          <Breadcrumb.Item onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>Home</Breadcrumb.Item>
          <Breadcrumb.Item onClick={() => store.dispatch(push(URL_PREFIX + "/courses/" + this.state.course.id ))}>
            {this.state.course.name}
          </Breadcrumb.Item>
          <Breadcrumb.Item active>
            {this.state.project.name}
          </Breadcrumb.Item>
        </Breadcrumb>

        <div className={styles.titleContainer}>
          <h2>{this.state.project.name}</h2>
        </div>

        <div className={styles.sectionContainer}>
          <Card>
            <Card.Body>
              <Card.Title>
                <h3 className={styles.sectionTitle}>Administration</h3>
              </Card.Title>

              <div>
                <Button variant="primary">
                  <Link className={styles.plainLink} to={this.props.match.url + "/groups"}>
                      Groups
                  </Link>
                </Button>

                <Button variant="primary">
                  <Link className={styles.plainLink} to={this.props.match.url + "/graders"}>
                      Manage graders
                  </Link>
                </Button>

                <Button variant="primary">
                  <Link className={styles.plainLink} to={this.props.match.url + "/grading"}>
                    Grading Interface
                  </Link>
                </Button>

                <Button variant="primary">
                  <Link className={styles.plainLink} to={this.props.match.url + "/feedback"}>
                    Feedback Interface
                  </Link>
                </Button>
              </div>
            </Card.Body>
          </Card>
        </div>

        <div className={styles.sectionContainer}>
          <Card>
            <Card.Body>
              <Card.Title>
                <h3 className={styles.sectionTitle}>Rubric</h3>
              </Card.Title>
              {this.props.rubric != null ?
                <div>
                  <Button variant="primary"><Link className={styles.plainLink} to={this.props.match.url + "/rubric"}>Open
                      rubric</Link></Button>
                  <Button variant="danger" onClick={this.onClickRemoveRubric}>Remove rubric</Button>
                </div>
                :
                <div>
                  <div>No rubric</div>
                  <Button variant="primary" onClick={this.onClickCreateRubric}>Create rubric</Button>
                </div>
              }
            </Card.Body>
          </Card>
        </div>
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    rubric: state.rubricNew.rubric
  };
};

const actionCreators = {
  saveRubric,
  deleteRubric
  // removeRubric
}

export default connect(mapStateToProps, actionCreators)(Project)