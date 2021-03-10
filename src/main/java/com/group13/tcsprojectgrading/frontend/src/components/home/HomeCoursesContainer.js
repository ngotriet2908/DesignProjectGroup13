import React, {Component} from "react";
import styles from "./home.module.css";
import CourseCard from "./CourseCard";

class HomeCoursesContainer extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return(
      <div className={styles.coursesContainer}>
        <h3 className={styles.sectionTitle}>Your Courses</h3>
        <ul className={styles.ul}>
          {this.props.courses.map(course => {
            return (
              <li className={styles.li} key={course.uuid}>
                <CourseCard data={course}/>
              </li>
            )
          })}
        </ul>
      </div>
    )
  }
}

export default HomeCoursesContainer;