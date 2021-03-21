import React, { Component } from 'react'
import PropTypes from 'prop-types'

import Card from 'react-bootstrap/Card'

import styles from './home.module.css'
import {URL_PREFIX} from "../../services/config";
import {COURSES} from "../../services/endpoints";
import {IoArrowForward, IoFlagOutline} from "react-icons/io5";
import store from "../../redux/store";
import {push} from "connected-react-router";
import globalStyles from '../helpers/global.module.css';
import classnames from 'classnames';

class ProjectCard extends Component {
  constructor (props) {
    super(props)
  }

  onClickSeeMore = () => {
    store.dispatch(push("/app/courses/" + this.props.data.course_id + "/projects/" + this.props.data.id));
  }

  render () {
    return (
      <Card className={[styles.card, styles.cardProject].join(" ")}>
        <Card.Body className={styles.cardBodyContainer}>
          <div className={styles.cardBodyTitle}>
            <h5>{this.props.data.name}</h5>
            <div className={classnames(globalStyles.iconButton, styles.cardButtonContainer)} onClick={this.onClickSeeMore}>
              <IoArrowForward size={26}/>
            </div>
          </div>
          <div className={styles.cardBodyContent}>
            <div>Created on {(new Date(this.props.data.created_at)).toDateString()}</div>
            {/*<div className={styles.cardButtonContainer}>*/}
            {/*<div onClick={this.onClickSeeMore}>*/}
            {/*  <IoArrowForward size={26}/>*/}
            {/*</div>*/}
            {/*</div>*/}
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
