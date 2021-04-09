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
import ButtonTooltip from "../helpers/ButtonTooltip";

class ProjectCard extends Component {
  constructor (props) {
    super(props)
  }

  onClickSeeMore = () => {
    store.dispatch(push("/app/courses/" + this.props.data.course.id + "/projects/" + this.props.data.id));
  }

  render () {
    return (
      <Card className={classnames(styles.card)}>
        <Card.Body className={styles.cardBodyContainer}>
          <div className={styles.cardBodyTitle}>
            <h5>{this.props.data.name}</h5>
            <ButtonTooltip className={classnames(globalStyles.iconButton, styles.cardButtonContainer)}
                           placement="bottom" content="Go to Project" onClick={this.onClickSeeMore}>
              <IoArrowForward size={26}/>
            </ButtonTooltip>
          </div>
          <div className={styles.cardBodyContent}>
            <div>Created on {(new Date(this.props.data.createdAt)).toDateString()}</div>
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
