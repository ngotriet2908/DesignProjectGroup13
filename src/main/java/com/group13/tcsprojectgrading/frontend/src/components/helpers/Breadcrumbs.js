import React, {Component} from "react";
import styles from "./breadcrumbs.module.css";
import ChevronRightIcon from '@material-ui/icons/ChevronRight';

class Breadcrumbs extends Component {
  constructor(props) {
    super(props);
  }

  static Item = (props) => {
    return (<BreadcrumbsItem active={props.active} onClick={props.onClick}>{props.children}</BreadcrumbsItem>);
  }

  render() {
    return(
      <div className={styles.breadcrumbsContainer}>
        {Array.isArray(this.props.children) ?
          this.props.children.map((child, index) => {
            return(
              <div key={index}>
                {child}
                {index !== this.props.children.length - 1 &&
              <span className={styles.breadcrumbsArrow}>
                <ChevronRightIcon/>
              </span>
                }
              </div>
            )
          })
          :
          this.props.children
        }
      </div>
    )
  }
}

export class BreadcrumbsItem extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return(
      this.props.active ?
        <span className={styles.breadcrumbsActive}>
          {this.props.children}
        </span>
        :
        <span className={styles.breadcrumbsLink} onClick={this.props.onClick}>
          {this.props.children}
        </span>
    )
  }
}

export default Breadcrumbs;