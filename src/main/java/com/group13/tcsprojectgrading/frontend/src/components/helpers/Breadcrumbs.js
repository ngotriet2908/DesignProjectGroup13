import React, {Component} from "react";
import styles from "./helpers.module.css";
import {IoChevronForward} from "react-icons/io5";

class Breadcrumbs extends Component {
  constructor(props) {
    super(props);

    // this.data = [
    //   {
    //     name: "Home",
    //     onClick: () => store.dispatch(push(URL_PREFIX + "/")),
    //   },
    //   {
    //     name: "Courses",
    //     onClick: () => store.dispatch(push(URL_PREFIX + "/")),
    //   },
    //   {
    //     name: "Projects",
    //     active: true,
    //   },
    // ]
  }

  render() {
    return(
      <div className={styles.breadcrumbsContainer}>
        {this.props.children.map((child, index) => {
          return (
            <div key={child.name}>
              {child.active ?
                <span className={styles.breadcrumbsActive}>
                  {child.name}
                </span>
                :
                <span className={styles.breadcrumbsLink} onClick={child.onClick}>
                  {child.name}
                </span>
              }
              {index !== this.props.children.length - 1 &&
                <span className={styles.breadcrumbsArrow}>
                  <IoChevronForward size={18}/>
                </span>
              }
            </div>
          )
        })}
      </div>
    )
  }
}

export default Breadcrumbs;