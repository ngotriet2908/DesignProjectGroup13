import React, { Component } from 'react'
import styles from './notFound.module.css'
import Button from "react-bootstrap/Button";
import {URL_PREFIX} from "../../services/config";
import {IoSadOutline} from "react-icons/io5";
import store from "../../redux/store";
import {push} from "connected-react-router";
import globalStyles from "../helpers/global.module.css";


class NotFound extends Component {
  constructor (props) {
    super(props)
  }

  render () {
    return (
      <div className={globalStyles.container}>
        <div className={styles.container}>
          {/*<div>*/}
          {/*  <IoSadOutline size={60}/>*/}
          {/*</div>*/}
          <h1 className={styles.title}>Oops... <IoSadOutline size={60}/></h1>
          <h2 className={styles.text}>It seems that we can't find the page you were looking for</h2>
          <Button variant="lightGreen" onClick={() => store.dispatch(push(URL_PREFIX + "/"))}>
            {/*<Link className={styles.plainLink} to={URL_PREFIX + "/"}>Home</Link>*/}
          Return to home
          </Button>
        </div>
      </div>
    )
  }
}

export default NotFound
