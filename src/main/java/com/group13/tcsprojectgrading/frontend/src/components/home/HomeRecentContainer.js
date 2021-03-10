import React, {Component} from "react";
import styles from "./home.module.css";
import ProjectCard from "./ProjectCard";

class HomeRecentContainer extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return(
      <div className={styles.recentContainer}>
        <h3 className={styles.sectionTitle}>Recent Activity</h3>
        <div>
          {this.props.recentProjects.length > 0 ?
            (<ul className={styles.ul}>
              {this.props.recentProjects.map(project => {
                return (
                  <li className={styles.li} key={project.id}>
                    <ProjectCard data={project}/>
                  </li>
                )
              })}
            </ul>)
            :
            (<div>
              <p>Work with projects to see your recent activity here.</p>
            </div>)
          }
        </div>
      </div>
    )
  }
}

export default HomeRecentContainer;