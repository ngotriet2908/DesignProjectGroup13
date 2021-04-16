import React, { Component } from 'react'
import styles from './home.module.css'
import classnames from "classnames";
import globalStyles from "../helpers/global.module.css";
import withTheme from "@material-ui/core/styles/withTheme";
import TodoList from "./TodoList";
import Paper from "@material-ui/core/Paper";
import Tab from "@material-ui/core/Tab";
import Tabs from "@material-ui/core/Tabs";
import IssueList from "./IssueList";


class TaskPanel extends Component {
  constructor (props) {
    super(props)

    this.state = {
      selected: 0,
    }

    this.components = [
      <TodoList
        key={0}
        tasks={this.props.tasks}
      />,
      <div
        key={1}
      >
        <IssueList
          issues={this.props.issues}
        />
      </div>
    ]
  }

  setSelected = (event, newValue) => {
    this.setState({
      selected: newValue
    })
  };

  render () {

    return(
      <div className={classnames(styles.taskContainer)}>
        <Paper
          className={styles.taskMenu}
        >
          <Tabs
            value={this.state.selected}
            onChange={this.setSelected}
            indicatorColor="primary"
            textColor="primary"
            classes={{
              indicator: styles.taskMenuIndicator,
              flexContainer: styles.taskMenuContainer
            }}
            TabIndicatorProps={{ children: <span style={{backgroundColor: this.props.theme.palette.primary.main}}/> }}
            centered
          >
            <Tab label="To-do" fullWidth/>
            <Tab label="Issues" fullWidth/>
          </Tabs>
        </Paper>

        {this.components[this.state.selected]}


      </div>
    )
  }
}

export default withTheme(TaskPanel);
