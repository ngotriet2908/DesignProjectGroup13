import React, { Component } from 'react'
import styles from './notFound.module.css'
import Button from "react-bootstrap/Button";
import {Link} from "react-router-dom";
import {URL_PREFIX} from "../../services/config";


class NotFound extends Component {
  constructor (props) {
    super(props)
  }

  render () {
    return (
      <div className={styles.container}>
        <h1>Page does not exist :(</h1>
        <Button variant="primary"><Link className={styles.plainLink} to={URL_PREFIX + "/"}>Home</Link></Button>
      </div>
    )
  }
}

export default NotFound
