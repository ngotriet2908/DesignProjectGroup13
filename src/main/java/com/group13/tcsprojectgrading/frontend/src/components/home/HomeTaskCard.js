import React, {Component} from "react";
import {Card, Button} from 'react-bootstrap'
import styles from './home.module.css'
import {Link} from "react-router-dom";
import {URL_PREFIX} from "../../services/config";
import {COURSES} from "../../services/endpoints";
import {IoArrowForward} from "react-icons/io5";
import store from "../../redux/store";
import {push} from "connected-react-router";

class HomeTaskCard extends Component {
  constructor(props) {
    super(props);
  }

  onClickSeeMore = () => {
    store.dispatch(push(`${URL_PREFIX}/${COURSES}/${this.props.task.course.id}/projects/${this.props.task.project.id}/tasks`));
  }

  render() {
    return(
    // <Card className={styles.cardBlock}>
    //   <Card.Body>
    //     <Card.Title>{this.props.task.project.name}</Card.Title>
    //     <h6>Course: {this.props.task.course.name}</h6>
    //     <h6>Number of tasks: {this.props.task.tasks}</h6>
    //     <h6>Progress: {this.props.task.progress}</h6>
    //     <Button variant="primary">
    //       <Link className={styles.plainLink} to={{
    //         pathname: `${URL_PREFIX}/${COURSES}/${this.props.task.course.id}/projects/${this.props.task.project.id}/tasks`
    //       }}>View Tasks
    //       </Link>
    //     </Button>
    //   </Card.Body>
    // </Card>

      <Card className={styles.card}>
        <Card.Body className={styles.cardBodyContainer}>
          <div className={styles.cardContentContainer}>
            <h5>{this.props.task.project.name}</h5>
            <div>Course: {this.props.task.course.name}</div>
            <div>Number of tasks: {this.props.task.tasks}</div>
          </div>
          <div className={styles.cardButtonContainer}>
            <div onClick={this.onClickSeeMore}>
              <IoArrowForward size={26}/>
            </div>
          </div>
        </Card.Body>
      </Card>
    )
  }
}

export default HomeTaskCard;