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


class TodoList extends Component {
  constructor (props) {
    super(props)
  }

  // TODO remove
  activity = () => {
    return(
      [
        {
          name: "Project 1",
          course: "Test course A (2020)",
          submissions: 4
        },
        {
          name: "Project 2",
          course: "Test course B (2020)",
          submissions: 2
        },
        {
          name: "Project 3",
          course: "Test course C (2020)",
          submissions: 21
        },
      ]
    )
  }

  render () {
    return(
      <div className={classnames(styles.todoContainer)}>
        <Card
          className={classnames(styles.todoCard)}
          style={{backgroundColor: this.props.theme.palette.additionalColors.lightBlue}}
        >
          <CardContent>
            <div className={styles.todoCardHeader}>
              <h3>
                  To-do list
              </h3>
            </div>
            <div className={styles.todoCardBody}>
              <List className={styles.root}>
                {this.activity().map((task) => {
                  return(
                    TodoItem(task)
                  )
                })}
              </List>
            </div>
          </CardContent>
        </Card>
      </div>
    )
  }
}

const TodoItem = (task) => {
  return(
    <ListItem alignItems="flex-start" key={task.name}>
      <ListItemText
        primary={task.name}
        secondary={
          <>
            <Typography
              component="span"
              variant="body1"
              className={styles.inline}
              color="primary"
            >
               in course {task.course}
            </Typography>
            {" — There are " + task.submissions + " submissions waiting for you… but first you need to make to-do actually work"}
          </>
        }
      />
    </ListItem>
  )
}

export default withTheme(TodoList);
