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
          {this.props.data.name}
        </h6>

        <h6 className={styles.taskCardDetails}>
          {this.props.data.submission_id}
        </h6>

      </div>
    )
  }

}

export default TaskCard