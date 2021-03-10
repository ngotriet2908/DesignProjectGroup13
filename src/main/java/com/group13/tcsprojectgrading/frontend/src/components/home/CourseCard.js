import React, { Component } from 'react'
import PropTypes from 'prop-types'

import Card from 'react-bootstrap/Card'

import styles from './home.module.css'
import {URL_PREFIX} from "../../services/config";
import {COURSES} from "../../services/endpoints";
import {IoArrowForward} from "react-icons/io5";
import store from "../../redux/store";
import {push} from "connected-react-router";

class CourseCard extends Component {
  constructor (props) {
    super(props)
  }

  onClickSeeMore = () => {
    store.dispatch(push(`${URL_PREFIX}/${COURSES}/${this.props.data.id}`));
  }

  render () {
    return (
      <Card className={styles.card}>
        <Card.Body className={styles.cardBodyContainer}>
          <div className={styles.cardContentContainer}>
            <h5>{this.props.data.name}</h5>
            <div>Active in year {(new Date(this.props.data.start_at)).getFullYear()}</div>
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

CourseCard.propTypes = {
  data: PropTypes.object
}

export default CourseCard
