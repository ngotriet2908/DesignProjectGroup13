import React, { Component } from 'react'
import PropTypes from 'prop-types'

import Card from 'react-bootstrap/Card'

import styles from './home.module.css'
import {URL_PREFIX} from "../../services/config";
import {COURSES} from "../../services/endpoints";
import {IoArrowForward} from "react-icons/io5";
import store from "../../redux/store";
import {push} from "connected-react-router";

class ProjectCard extends Component {
  constructor (props) {
    super(props)
  }

  onClickSeeMore = () => {
    store.dispatch(push("/app/courses/" + this.props.data.course_id + "/projects/" + this.props.data.id));
  }

  render () {
    return (
      <Card className={styles.card}>
        <Card.Body className={styles.cardBodyContainer}>
          <div className={styles.cardContentContainer}>
            <h5>{this.props.data.name}</h5>
            <div>Created on {(new Date(this.props.data.created_at)).toDateString()}</div>
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

ProjectCard.propTypes = {
  data: PropTypes.object
}

export default ProjectCard
