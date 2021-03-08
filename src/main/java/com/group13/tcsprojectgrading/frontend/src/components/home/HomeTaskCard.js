import React, {Component} from "react";
import {Card, Button} from 'react-bootstrap'
import styles from './home.module.css'
import {Link} from "react-router-dom";
import {URL_PREFIX} from "../../services/config";
import {COURSES} from "../../services/endpoints";

class HomeTaskCard extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return(
      <Card className={styles.cardBlock}>
        <Card.Body>
          <Card.Title>{this.props.task.project.name}</Card.Title>
          <h6>Course: {this.props.task.course.name}</h6>
          <h6>Number of tasks: {this.props.task.tasks}</h6>
          <h6>Progress: {this.props.task.progress}</h6>
          <Button variant="primary">
            <Link className={styles.plainLink} to={{
              pathname: `${URL_PREFIX}/${COURSES}/${this.props.task.course.id}/projects/${this.props.task.project.id}/tasks`
            }}>View Tasks
            </Link>
          </Button>
        </Card.Body>
      </Card>
    )
  }
}

export default HomeTaskCard;