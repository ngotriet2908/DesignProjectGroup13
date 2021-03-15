import React, {Component} from "react";
import {Card} from 'react-bootstrap'
import styles from './home.module.css'
import {URL_PREFIX} from "../../services/config";
import {COURSES} from "../../services/endpoints";
import {IoArrowForward} from "react-icons/io5";
import store from "../../redux/store";
import {push} from "connected-react-router";

class HomeTaskCard extends Component {
  constructor(props) {
    super(props);

    console.log(this.props.data);
  }

  onClickSeeMore = () => {
    store.dispatch(push(`${URL_PREFIX}/${COURSES}/${this.props.data.course.id}/projects/${this.props.data.project.id}/tasks`));
  }

  render() {
    return(
      <Card className={styles.card}>
        <Card.Body className={styles.cardBodyContainer}>
          <div className={styles.cardContentContainer}>
            <h5>{this.props.data.project.name}</h5>
            <div>Course: {this.props.data.course.name}</div>
            <div>Number of tasks: {this.props.data.tasks}</div>
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