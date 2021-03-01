import React, { Component } from 'react'

import styles from './project.module.css'
import {request} from "../../services/request";
import {BASE, PROJECT, RUBRIC_CURRENT, USER_COURSES, USER_INFO} from "../../services/endpoints";
import {Link} from "react-router-dom";
import Button from "react-bootstrap/Button";
import {URL_PREFIX} from "../../services/config";
import {v4 as uuidv4} from "uuid";
import {appendBlock, saveRubric, moveCriterion, removeRubric, reorderCriterion} from "../../redux/rubric/actions";
import {connect} from "react-redux";
// import ProjectCard from '../projects/ProjectCard'

class Project extends Component {
  constructor (props) {
    super(props)
    this.state = {
      loaded: false,
    }
  }

  componentDidMount () {
    request(BASE + PROJECT)
      .then(response => {
        console.log(response);
        return response.json();
      })
      .then(data => {
        console.log(data);
        this.setState({
          loaded: true
        })
        this.props.loadRubric(data.rubric);
      })
      .catch(error => {
        console.error(error.message);
        this.setState({
          loaded: true
        })
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

    request(BASE + RUBRIC_CURRENT, "POST", rubric)
      .then(data => {
        console.log(data);

        this.props.loadRubric(rubric);
      })
      .catch(error => {
        console.error(error.message);
      });
  }

  // postRubric = () => {
  //   request(BASE + RUBRIC_CURRENT, "POST", this.props.rubric)
  //     .then(data => {
  //       console.log(data);
  //     })
  //     .catch(error => {
  //       console.error(error.message);
  //     });
  // }

  onClickRemoveRubric = () => {
    this.props.removeRubric();
  }

  render () {
    return (
      <div className={styles.container}>
        <h1>Programming Project</h1>
        <h2>General</h2>
        <div>Blablabla</div>

        <h2>TAs</h2>
        <div>Blablabla</div>

        {this.props.rubric != null ?
          <div>
            <h2>My rubric</h2>
            <Button variant="primary"><Link className={styles.plainLink} to={URL_PREFIX + '/projects/34/rubric/'}>Open
              rubric</Link></Button>
            <Button variant="danger" onClick={this.onClickRemoveRubric}>Remove rubric</Button>
          </div>
          :
          <div>
            <div>No rubric</div>
            <Button variant="primary" onClick={this.onClickCreateRubric}>Create rubric</Button>
          </div>
        }
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
  loadRubric: saveRubric,
  removeRubric
}

export default connect(mapStateToProps, actionCreators)(Project)
