import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'

import Card from 'react-bootstrap/Card'
import Button from 'react-bootstrap/Button'

import styles from './course.module.css'

class ProjectCard extends Component {

  constructor (props) {
    super(props)

    this.state = {
      project: props.data
    }
  }

  src = [
    "https://i.pinimg.com/564x/d5/c0/d9/d5c0d9d759e10be5792ad81c0cc01762.jpg",
    "https://i.pinimg.com/564x/37/eb/1f/37eb1f1453d2c27e40b64dbf2e7f60a4.jpg",
    "https://i.pinimg.com/564x/4a/1e/c9/4a1ec930da2bd34861539f05bb48169e.jpg",
    "https://i.pinimg.com/564x/9d/28/89/9d288904f790fc61da9caf169b88796b.jpg",
    "https://i.pinimg.com/564x/66/77/31/667731236130db12485972ff83f641ad.jpg",
    "https://i.pinimg.com/564x/dc/d9/35/dcd9350db4d7210ac4528b3b981b848a.jpg",
    "https://i.pinimg.com/564x/fb/32/ba/fb32bad50558de578d3ec490c330aaa7.jpg",
  ];

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
