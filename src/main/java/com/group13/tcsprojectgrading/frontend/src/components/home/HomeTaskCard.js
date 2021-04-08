import React, {Component} from "react";
import {Card} from 'react-bootstrap'
import styles from './home.module.css'
import {URL_PREFIX} from "../../services/config";
import {COURSES} from "../../services/endpoints";
import {IoArrowForward} from "react-icons/io5";
import store from "../../redux/store";
import {push} from "connected-react-router";
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";

class HomeTaskCard extends Component {
  constructor(props) {
    super(props);
  }

  onClickSeeMore = () => {
    store.dispatch(push(`${URL_PREFIX}/${COURSES}/${this.props.data.course.id}/projects/${this.props.data.project.id}/submissions`));
  }

  render() {
    return(
      <Card className={styles.card}>
        <Card.Body className={styles.cardBodyContainer}>
          <div className={styles.cardBodyTitle}>
            <h5>{this.props.data.project.name}</h5>
            <div className={classnames(globalStyles.iconButton, styles.cardButtonContainer)} onClick={this.onClickSeeMore}>
              <IoArrowForward size={26}/>
            </div>
          </div>
          <div className={styles.cardBodyContent}>
            <div>Course: {this.props.data.course.name}</div>
            <div>Number of tasks: {this.props.data.submissions}</div>
          </div>
        </Card.Body>
      </Card>
    )
  }
}

export default HomeTaskCard;