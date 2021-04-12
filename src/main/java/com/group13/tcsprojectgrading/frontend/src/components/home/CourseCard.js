import React, { Component } from 'react'

import styles from './home.module.css'
import {URL_PREFIX} from "../../services/config";
import {COURSES} from "../../services/endpoints";
import store from "../../redux/store";
import {push} from "connected-react-router";
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import AccessTimeIcon from '@material-ui/icons/AccessTime';
import PersonIcon from '@material-ui/icons/Person';
import Link from "@material-ui/core/Link";


class CourseCard extends Component {
  constructor (props) {
    super(props)
  }

  onClickSeeMore = () => {
    store.dispatch(push(`${URL_PREFIX}/${COURSES}/${this.props.data.id}`));
  }

  render () {
    return (
      <Card elevation={1} className={classnames(styles.courseCard, globalStyles.cardShadow)}>
        <CardContent>
          <div className={styles.cardBodyTitle}>
            <Link href="#" color="primary" onClick={(event) => {
              event.preventDefault();
              this.onClickSeeMore();
            }}>
              <h4>{this.props.data.name}</h4>
            </Link>

          </div>
          <div className={styles.cardBodyContent}>
            <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
              <AccessTimeIcon/> <span>Active in {(new Date(this.props.data.startAt)).getFullYear()}</span>
            </div>
            <div className={classnames(globalStyles.flexRow, globalStyles.flexRowWithIcon)}>
              <PersonIcon/> <span>3 students</span>
            </div>
          </div>
        </CardContent>
      </Card>
    )
  }
}

export default CourseCard;
