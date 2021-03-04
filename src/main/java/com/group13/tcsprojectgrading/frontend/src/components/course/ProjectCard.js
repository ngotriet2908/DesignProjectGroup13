import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'

import Card from 'react-bootstrap/Card'
import Button from 'react-bootstrap/Button'

import styles from './course.module.css'
import {animeSources, normalSources} from "./ImageSources";

class ProjectCard extends Component {

  constructor (props) {
    super(props)

    this.state = {
      project: props.data
    }
  }

  // src = animeSources
  src = normalSources

  randomSrc() {
    return this.src[Math.floor(Math.random() * this.src.length)];
  }

  render () {
    return (
      <Card className={styles.card}>
        <Card.Img className={styles.cardImg} height="150" variant="cover" src={this.randomSrc()}/>
        <Card.Body>
          <Card.Title>{this.state.project.name}</Card.Title>
          <Card.Text>
            Created at: {this.state.project.created_at}
          </Card.Text>
          <Button variant="primary">
            <Link className={styles.plainLink} to={{
              pathname: "/app/courses/" + this.state.project.course_id + "/projects/" + this.state.project.id
            }}>Open
            </Link>
          </Button>
        </Card.Body>
      </Card>
    )
  }
}

ProjectCard.propTypes = {
  data: PropTypes.object
}

export default ProjectCard
