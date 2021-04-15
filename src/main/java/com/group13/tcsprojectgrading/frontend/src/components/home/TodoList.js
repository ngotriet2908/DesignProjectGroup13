import React, { Component } from 'react'
import styles from './home.module.css'
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
import CardContent from "@material-ui/core/CardContent";
import Card from "@material-ui/core/Card";
import withTheme from "@material-ui/core/styles/withTheme";
import Typography from "@material-ui/core/Typography";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import AssignmentIcon from '@material-ui/icons/Assignment';
import store from "../../redux/store";
import {push} from "connected-react-router";
import Link from "@material-ui/core/Link";
import Pluralize from "pluralize";
import DoneIcon from '@material-ui/icons/Done';


class TodoList extends Component {
  constructor (props) {
    super(props)
  }

  render () {
    return(
      <Card
        className={classnames(styles.todoCard)}
        style={{backgroundColor: this.props.theme.palette.additionalColors.lightBlue}}
      >
        <CardContent className={classnames(styles.todoCardContent)}>
          <div className={styles.todoCardHeader}>
            <h4>
                  To-do list
            </h4>
          </div>

          {this.props.tasks.length > 0 ?

            <div className={styles.todoCardBody}>
              <List className={styles.root}>
                {this.props.tasks.map((task) => {
                  return (
                    TodoItem(task)
                  )
                })}
              </List>
            </div>
            :
            <div className={styles.todoCardCenteredBody}>
                No tasks <DoneIcon/>
            </div>
          }

        </CardContent>
      </Card>
    )
  }
}

const TodoItem = (task) => {
  return(
    <ListItem alignItems="flex-start" key={task.name}>
      <ListItemAvatar>
        <Avatar>
          <AssignmentIcon />
        </Avatar>
      </ListItemAvatar>

      <ListItemText
        primary={
          <Link href="#" onClick={(event) => {
            event.preventDefault();
            store.dispatch(push("/app/courses/" +
              task.course.id +
              "/projects/" +
              task.id +
              "/submissions", {status: 1}));
          }}>
            {task.name}
          </Link>
        }
        secondary={
          <>
            <Typography
              component="span"
              variant="body1"
              style={{display: "block"}}
            >
               in {task.course.name}
            </Typography>

            <Typography
              component="span"
              variant="body1"
              style={{display: "block"}}
            >
              {Pluralize('submission', task.gradingTasks, true)} to grade
            </Typography>
          </>
        }
      />
    </ListItem>
  )
}

export default withTheme(TodoList);
