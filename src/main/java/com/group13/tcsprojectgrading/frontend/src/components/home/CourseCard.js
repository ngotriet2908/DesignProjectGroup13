import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'

import Card from 'react-bootstrap/Card'
import Button from 'react-bootstrap/Button'

import styles from './home.module.css'
import {BASE} from "../../services/endpoints";
import {URL_PREFIX} from "../../services/config";

class CourseCard extends Component {
  constructor (props) {
    super(props)

    this.state = {
      course: props.data
    }
  }

  render () {
    return (
      <Card className={styles.card}>
        <Card.Img height="150" variant="cover" src="https://picsum.photos/seed/picsum/536/354" />
        <Card.Body>
          <Card.Title>{this.state.course.name}</Card.Title>
          <Card.Text>
            {/*{this.state.course.year}*/}
            2021
          </Card.Text>
          <Button variant="primary"><Link className={styles.plainLink} to={URL_PREFIX + "/projects/34"}>Open course</Link></Button>
        </Card.Body>
      </Card>
    )
  }
}

CourseCard.propTypes = {
  data: PropTypes.object
}

export default CourseCard
