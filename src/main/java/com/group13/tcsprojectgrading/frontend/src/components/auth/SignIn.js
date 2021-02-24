import React, { Component } from 'react'
import styles from './auth.module.css'
import Card from 'react-bootstrap/Card'
import Button from 'react-bootstrap/Button'

class SignIn extends Component {
  constructor (props) {
    super(props)
    this.state = {

    }

    // console.log("Sign in rendered.")
  }

  login = () => {
    fakeAuth.authenticate(() => {
      this.setState({
        // redirectToReferrer: true
      })
    })
  };

  render () {
    return (
      <div className={styles.container}>
        <Card className={styles.card}>
          <Card.Body>
            <Card.Title>Grading System</Card.Title>
            <Button variant="primary" href="/oauth2/authorization/canvas">
                Sign in via UT
            </Button>
          </Card.Body>
        </Card>
      </div>
    )
  }
}

/* A fake authentication function */
export const fakeAuth = {
  isAuthenticated: false,
  authenticate(cb) {
    this.isAuthenticated = true;
    setTimeout(cb, 100)
  },
};

export default SignIn
