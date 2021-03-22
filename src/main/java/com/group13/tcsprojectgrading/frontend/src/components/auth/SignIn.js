import React, { Component } from 'react'
import styles from './auth.module.css'
import Card from 'react-bootstrap/Card'
import Button from 'react-bootstrap/Button'
import {HOST} from "../../services/endpoints";

class SignIn extends Component {
  constructor (props) {
    super(props)
  }

  render () {
    return (
      <div className={styles.container}>
        <Card className={styles.card}>
          <Card.Body>
            <div className={styles.logoContainer}>
              <img height="40" width="40" src={HOST + "/img/logo.png"} alt="logo"/>
              <h1>ProGrader</h1>
            </div>
            <Button className={styles.signInButton} size="lg" variant="lightGreen" href="/oauth2/authorization/canvas">
                Sign in with UT Canvas
            </Button>
          </Card.Body>
        </Card>
        <div className={styles.stripesContainer}>
        </div>
      </div>
    )
  }
}

export default SignIn
