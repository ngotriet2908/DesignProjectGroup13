import React, {Component} from "react";
import styles from "./home.module.css";
import HomeTaskCard from "./HomeTaskCard";

class HomeTasksContainer extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return(
      <div className={styles.homeTasksContainer}>
        <h3 className={styles.sectionTitle}>Your Tasks</h3>
        <div>
          {this.props.tasks.length > 0 ?
            <ul className={styles.ul}>
              {this.props.tasks.map(task => {
                return (
                  <li className={styles.li} key={task.project.id + "/" + task.course.id}>
                    <HomeTaskCard task={task}/>
                  </li>
                )
              })}
            </ul>
            :
            (<div>
              <p>You don't have any active tasks.</p>
            </div>)
          }
        </div>
      </div>
    )
  }
}

export default HomeTasksContainer;