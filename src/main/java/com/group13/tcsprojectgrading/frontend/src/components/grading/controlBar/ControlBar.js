import React, { Component } from 'react'
import styles from '../grading.module.css'


class ControlBar extends Component {
  constructor (props) {
    super(props)

  }

  render () {
    return (
      <div className={styles.controlBarContainer}>
        <div className={styles.controlBarBody}>
          <h4>
            {this.props.submission.name}
          </h4>
        </div>
      </div>
    )
  }
}

export default ControlBar;
