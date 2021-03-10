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
            {/*<Card.Text><img height="50" width="50" src="http://localhost:8080/img/logo.png"/></Card.Text>*/}
            {/*<Card.Title><h2>ProGrader</h2></Card.Title>*/}
            <div className={styles.logoContainer}>
              <img height="40" width="40" src="http://localhost:8080/img/logo.png" alt="logo"/>
              <h2>ProGrader</h2>
            </div>
            <Button size="lg" variant="primary" href="/oauth2/authorization/canvas">
                Sign in with UT
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
