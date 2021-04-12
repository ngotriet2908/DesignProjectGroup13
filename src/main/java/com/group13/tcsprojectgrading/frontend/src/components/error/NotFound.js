import React, { Component } from 'react'
import styles from './notFound.module.css'
import {URL_PREFIX} from "../../services/config";
import store from "../../redux/store";
import {push} from "connected-react-router";
import globalStyles from "../helpers/global.module.css";
import Button from "@material-ui/core/Button";
import SentimentDissatisfiedIcon from '@material-ui/icons/SentimentDissatisfied';
import classnames from 'classnames';

class NotFound extends Component {
  constructor (props) {
    super(props)
  }

  render () {
    return (
      <div className={classnames(globalStyles.innerScreenContainer, styles.screenContainer)}>
        <div >
          <h1 className={styles.title}>
            Oops...
            <SentimentDissatisfiedIcon style={{ fontSize: 70 }}/>
          </h1>
          <h2 className={styles.text}>It seems that we can't find the page you were looking for</h2>
          <Button
            color={"primary"}
            onClick={() => store.dispatch(push(URL_PREFIX + "/"))}
            disableElevation
            variant="contained"
          >
            Return to home
          </Button>
        </div>
      </div>
    )
  }
}

export default NotFound
