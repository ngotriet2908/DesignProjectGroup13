import React, { Component } from 'react'
import PropTypes from 'prop-types'

import Card from 'react-bootstrap/Card'

import styles from './home.module.css'
import {IoArrowForward} from "react-icons/io5";

class StatsCard extends Component {
  constructor (props) {
    super(props)
  }

  render () {
    return (
      <Card className={styles.card}>
        <Card.Body className={styles.cardBodyContainer}>
          <div className={styles.cardContentContainer}>
            <h5>Students</h5>
            <div>Number of students: 4</div>
          </div>
          <div className={styles.cardButtonContainer}>
            {/*<div onClick={this.onClickSeeMore}>*/}
            {/*  <IoArrowForward size={26}/>*/}
            {/*</div>*/}
          </div>
        </Card.Body>
      </Card>
    )
  }
}

// StatsCard.propTypes = {
//   data: PropTypes.object
// }

export default StatsCard
