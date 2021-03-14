import React, { Component } from 'react'
import Card from 'react-bootstrap/Card'

import styles from './home.module.css'
import {URL_PREFIX} from "../../services/config";
import {COURSES} from "../../services/endpoints";
import {IoArrowForward} from "react-icons/io5";
import store from "../../redux/store";
import {push} from "connected-react-router";
import {IoCafe, IoCheckboxOutline} from "react-icons/io5";

class EmptyCard extends Component {
  constructor (props) {
    super(props)
  }

  render () {
    return (
      <Card className={[styles.cardEmpty].join(" ")}>
        <Card.Body className={styles.emptyCardBodyContainer}>
          <div className={styles.emptyCardContentContainer}>
            {/*<IoCheckboxOutline size={60}/>*/}
            {this.props.icon &&
              <this.props.icon className={styles.emptyCardContentIcon} size={60}/>
            }
            <h5>{this.props.data}</h5>
          </div>
        </Card.Body>
      </Card>
    )
  }
}

export default EmptyCard
