import React, {Component} from "react";
import styles from "./assign.module.css";

class TaskCard extends Component {
  constructor (props) {
    super(props)
    this.state = {
      group: props.data
    }
  }

  render() {
    return (
      <div className={styles.taskCard}>
        <h6 className={styles.taskCardGroupName}>
          {/*{this.state.group.name}*/}
          {this.props.data.name}
        </h6>

        <h6 className={styles.taskCardDetails}>
          {/*/!*{this.state.group.name}*!/*/}
          {/*{this.props.data.members.map((member) => {*/}
          {/*  return member.name*/}
          {/*}).join(", ")}*/}
          {this.props.data.progress}% graded, member#: {this.props.data.members.length}, member: {this.props.data.members.map((member) => {return member.name}).join(", ")}
        </h6>

      </div>
    )
  }

}

export default TaskCard