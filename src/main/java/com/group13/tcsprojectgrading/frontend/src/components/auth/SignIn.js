import React, { Component } from 'react'
import styles from './auth.module.css'
import {HOST} from "../../services/endpoints";
import Button from "@material-ui/core/Button";

class SignIn extends Component {
  constructor (props) {
    super(props)
  }

  render () {
    return (
      <div className={styles.screenContainer}>
        <div className={styles.card}>
          <div>
            <div className={styles.logoContainer}>
              <img height="40" width="40" src={HOST + "/img/logo.png"} alt="logo"/>
              <h1>ProGrader</h1>
            </div>

            <Button
              className={styles.plainLink}
              variant="contained"
              size="large"
              color="primary"
              href="/oauth2/authorization/canvas">
                Sign in with UT Canvas
            </Button>
          </div>
        </div>

        <div className={styles.stripesContainer}>
        </div>
      </div>
    )
  }
}

export default SignIn
