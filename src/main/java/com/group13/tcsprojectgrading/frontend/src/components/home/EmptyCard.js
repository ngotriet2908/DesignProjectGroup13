import React, { Component } from 'react'
import Card from 'react-bootstrap/Card'

import styles from './home.module.css'
import classnames from 'classnames';

class EmptyCard extends Component {
  constructor (props) {
    super(props)
  }

  render () {
    return (
      <Card className={classnames(styles.cardEmpty)}>
        <Card.Body className={styles.emptyCardBodyContainer}>
          <div className={styles.emptyCardContentContainer}>
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
